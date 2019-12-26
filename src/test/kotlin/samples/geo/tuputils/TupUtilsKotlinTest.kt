package samples.geo.tuputils

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.test.StepVerifier

class TupUtilsKotlinTest {
    @Test
    fun testZipWithoutTupUtils() {
        val flux: Flux<String> = Mono.zip(Mono.just("a"), Mono.just(2))
            .flatMapMany { tup ->
                val s = tup.t1
                val count = tup.t2
                Flux.range(1, count)
                    .map { i: Int -> s + i }
            }
        StepVerifier
            .create(flux)
            .expectNext("a1")
            .expectNext("a2")
            .verifyComplete()
    }

    @Test
    fun testZipWithDestructuring() {
        val flux: Flux<String> = Mono.zip(Mono.just("a"), Mono.just(2))
            .flatMapMany { (s: String, count: Int) ->
                Flux.range(1, count).map { i: Int -> s + i }
            }

        StepVerifier
            .create(flux)
            .expectNext("a1")
            .expectNext("a2")
            .verifyComplete()
    }
}