package samples.geo.tuputils;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;
import reactor.test.StepVerifier;

class TupUtilsTest {

    @Test
    void testZipWithoutTupUtils() {
        Flux<String> flux = Mono.zip(Mono.just("a"), Mono.just(2))
                .flatMapMany(tup -> {
                    String s = tup.getT1();
                    int count = tup.getT2();

                    return Flux.range(1, count).map(i -> s + i);
                });

        StepVerifier
                .create(flux)
                .expectNext("a1")
                .expectNext("a2")
                .verifyComplete();
    }

    @Test
    void testZipWithTupUtils() {
        Flux<String> flux = Mono.zip(Mono.just("a"), Mono.just(2))
                .flatMapMany(TupleUtils.function((s, count) ->
                        Flux.range(1, count).map(i -> s + i)));

        StepVerifier
                .create(flux)
                .expectNext("a1")
                .expectNext("a2")
                .verifyComplete();
    }
}
