package samples.geo;

import org.junit.jupiter.api.Disabled;
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
import java.util.List;
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
                .subscribeOn(Schedulers.newParallel("sub"))
                .publishOn(Schedulers.newParallel("pub"))
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
        Flux<Integer> source = Flux.fromStream(() -> {
            LOGGER.info("A costly call!");
            return List.of(1, 2, 3).stream();
        }).doOnNext(n -> LOGGER.info("doOnNext hook: {}", n));

        CountDownLatch latch = new CountDownLatch(2);

        source.subscribe(
                n -> LOGGER.info("sub 1: {}", n),
                t -> {
                    LOGGER.error(t.getMessage(), t);
                    latch.countDown();
                },
                () -> {
                    latch.countDown();
                }
        );

        source.subscribe(
                n -> LOGGER.info("sub 2: {}", n),
                t -> {
                    LOGGER.error(t.getMessage(), t);
                    latch.countDown();
                },
                () -> {
                    latch.countDown();
                }
        );

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void test_03_HotPublisher() throws InterruptedException {
        Flux<Integer> source = Flux.fromStream(() -> {
            LOGGER.info("A costly call!");
            return List.of(1, 2, 3).stream();
        })
                .subscribeOn(Schedulers.elastic())
                .doOnNext(n -> LOGGER.info("doOnNext hook: {}", n))
                .publish()
                .autoConnect();

        CountDownLatch latch = new CountDownLatch(2);

        source.subscribe(
                n -> LOGGER.info("sub 1: {}", n),
                t -> {
                    LOGGER.error(t.getMessage(), t);
                    latch.countDown();
                },
                () -> {
                    latch.countDown();
                }
        );

        source.subscribe(
                n -> LOGGER.info("sub 2: {}", n),
                t -> {
                    LOGGER.error(t.getMessage(), t);
                    latch.countDown();
                },
                () -> {
                    latch.countDown();
                }
        );

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    @Disabled
    public void test_04_back_pressure() throws InterruptedException {
        Flux<Long> flux = Flux
                .generate(() -> 1L, (Long state, SynchronousSink<Long> sink) -> {
                    Long nextState = state + 1L;
                    if (nextState > 1_000_000) {
                        sink.complete();
                    } else {
                        sink.next(nextState);
                        LOGGER.info("Emitted {}", nextState);
                    }
                    return state + 1;
                })
                .subscribeOn(Schedulers.newParallel("sub"))

                .flatMap(state -> Flux.just(state), 10)
                .publishOn(Schedulers.newElastic("pub"), 5)
                .doOnNext(state -> {
                    LOGGER.info("doOnNext {}", state);
                });


        // .doOnNext(n -> LOGGER.info("doOnNext {}", n))

        // .publishOn(Schedulers.newParallel("pub"));


        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> flux
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(5);
                    }

                    @Override
                    protected void hookOnNext(Long value) {
                        Utils.sleep(200);
                        LOGGER.info("Got {}", value);
                        request(5);
                        // cancel();
                    }
                })).start();

        latch.await(100, TimeUnit.SECONDS);
    }
}