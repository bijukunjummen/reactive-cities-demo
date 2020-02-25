package samples.geo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.stream.IntStream;

class ReactiveBasics {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveBasics.class);

    /**
     * Demonstrates creating a flux
     * <p>
     * Assembly time and Execution Time.
     */
    @Test
    void test_01_creating_a_flux() {
        Flux<String> flux = Flux.just(
                "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
        );

        flux.subscribe(
                l -> LOGGER.info("Got {}", l),
                t -> t.printStackTrace(),
                () -> LOGGER.info("DONE!!")
        );
    }

    /**
     * Demonstrates a Mono..
     */
    @Test
    void test_02_creating_a_mono() {
        Mono<String> mono = Mono.just("zero");
        // mono.subscribe(
        //         str -> LOGGER.info(str),
        //         t -> t.printStackTrace(),
        //         () -> LOGGER.info("DONE!!")
        // );
    }

    @Test
    void test_03_mapping() {
        var flux = Flux.just(
                "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
                .map(str -> {
                    return Tuples.of(str, str.length());
                });

        flux.subscribe(
                l -> LOGGER.info("Got {}", l),
                t -> t.printStackTrace(),
                () -> LOGGER.info("DONE!!")
        );
    }

    @Test
    void test_04_flat_map() {
        var flux = Flux.just(
                "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
                .flatMap(str -> Flux.range(0, str.length()).map(n -> str));

        flux.

                subscribe(
                        l -> LOGGER.info("Got {}", l),
                        t -> t.printStackTrace(),
                        () -> LOGGER.info("DONE!!")
                );
    }

    @Test
    void testMono() {
        var mono = Mono.just("1");
        mono
                .doOnSuccess(s -> LOGGER.info("From Success:" + s))
                .doOnError(t -> LOGGER.error(t.getMessage(), t))
                .doFinally((SignalType signalType) -> {
                    LOGGER.info(signalType.toString());
                }).block();

    }

    @Test
    void testMonoExpand() {
        var mono = Mono.just("1");
        mono
                .doOnSuccess(s -> LOGGER.info("From Success:" + s))
                .doOnError(t -> LOGGER.error(t.getMessage(), t))
                .doFinally((SignalType signalType) -> {
                    LOGGER.info(signalType.toString());
                }).block();
        System.out.println(Flux.empty().collectList().block());
    }


}
