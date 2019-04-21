package samples.geo;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static samples.geo.Utils.*;

public class ReactiveExecutionModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveExecutionModel.class);

    /**
     * Concurrency Agnostic
     */
    @Test
    public void test_01_concurrency_agnostic() throws Exception {
        Flux<String> flux = Flux.just("one", "two", "three", "four", "five", "six")
                .map(s -> {
                    LOGGER.info("map1 {}", s);
                    return s + " " + s;
                })
                // .subscribeOn(Schedulers.newParallel("sub"))
                // .publishOn(Schedulers.newParallel("pub"))
                .map(s -> {
                    LOGGER.info("map2 {}", s);
                    return s + " " + s;
                });

        CountDownLatch latch = new CountDownLatch(1);

        flux
                .subscribe(str -> LOGGER.info("Got {}", str),
                        t -> {
                            LOGGER.error(t.getMessage(), t);
                            latch.countDown();
                        },
                        () -> latch.countDown()
                );

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void test_02_ColdPublisher() throws InterruptedException {
        Flux<Integer> source = Flux.range(1, 5)
                .doOnNext(n -> LOGGER.info("doOnNext hook: {}", n));

        // source.subscribe(
        //         n -> LOGGER.info("sub 1: {}", n),
        //         t -> LOGGER.error(t.getMessage(), t),
        //         () -> {
        //         }
        // );
        //
        // source.subscribe(
        //         n -> LOGGER.info("sub 2: {}", n),
        //         t -> LOGGER.error(t.getMessage(), t),
        //         () -> {
        //         }
        // );
    }

    @Test
    public void test_03_HotPublisher() throws InterruptedException {
        Flux<Integer> source = Flux.range(1, 5)
                // .delayElements(Duration.ofMillis(200))
                .doOnNext(n -> LOGGER.info("doOnNext hook: {}", n))
                .publish()
                .autoConnect();

        source.subscribe(
                n -> LOGGER.info("sub 1: {}", n),
                t -> LOGGER.error(t.getMessage(), t),
                () -> {
                }
        );

        // Thread.sleep(500);
        // source.subscribe(
        //         n -> LOGGER.info("sub 2: {}", n),
        //         t -> LOGGER.error(t.getMessage(), t),
        //         () -> {
        //         }
        // );
        //
        // Thread.sleep(5000);
    }

    @Test
    public void test_04_back_pressure() throws InterruptedException {
        Flux<Integer> flux = Flux
                .range(1, 5000)
                .doOnNext(n -> LOGGER.info("doOnNext {}", n))
                .subscribeOn(Schedulers.newParallel("sub"));
        // .publishOn(Schedulers.newParallel("pub"));


        CountDownLatch latch = new CountDownLatch(1);

        flux
                .publishOn(Schedulers.newParallel("pub"))
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(5);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        LOGGER.info("Got {}", value);
                        Utils.sleep(20);
                        request(5);
                        // cancel();
                    }
                });
        latch.await(10, TimeUnit.SECONDS);
    }
}