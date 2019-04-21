package samples.geo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.stream.IntStream;

public class ReactiveBasics {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveBasics.class);

    /**
     * Demonstrates creating a flux
     * <p>
     * Assembly time and Execution Time.
     */
    @Test
    public void test_01_creating_a_flux() {
        Flux<String> flux = Flux.just(
                "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
        );

        // flux.subscribe(
        //         l -> LOGGER.info("Got {}", l),
        //         t -> t.printStackTrace(),
        //         () -> LOGGER.info("DONE!!")
        // );
    }

    /**
     * Demonstrates a Mono..
     */
    @Test
    public void test_02_creating_a_mono() {
        Mono<String> mono = Mono.just("zero");
        // mono.subscribe(
        //         str -> LOGGER.info(str),
        //         t -> t.printStackTrace(),
        //         () -> LOGGER.info("DONE!!")
        // );
    }

    @Test
    public void test_03_mapping() {
        var flux = Flux.just(
                "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
                // .map(str -> {
                //     return Tuples.of(str, str.length());
                // });

        // flux.subscribe(
        //         l -> LOGGER.info("Got {}", l),
        //         t -> t.printStackTrace(),
        //         () -> LOGGER.info("DONE!!")
        // );
    }

    @Test
    public void test_04_flat_map() {
        var flux = Flux.just(
                "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
        // .flatMap(str -> {
        //     return Flux.range(0, str.length()).map(n -> str);
        // });

        // flux.subscribe(
        //         l -> LOGGER.info("Got {}", l),
        //         t -> t.printStackTrace(),
        //         () -> LOGGER.info("DONE!!")
        // );
    }


}
