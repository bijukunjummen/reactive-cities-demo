package samples.geo

import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import samples.geo.domain.City
import java.util.concurrent.CountDownLatch

@Ignore
class CitiesReactorKotlinTest {

    private val webClient = WebClient.builder().baseUrl("http://localhost:9090").build()

    private val logger = LoggerFactory.getLogger(CitiesReactorKotlinTest::class.java)

    @Test
    fun getCityDescriptions() {
        val cityIds: Flux<List<Long>> = webClient.get()
                .uri("/cityids")
                .exchange()
                .flatMapMany { response: ClientResponse ->
                    response.bodyToMono<List<Long>>()
                }

        val cityIdsFlux: Flux<Long> = cityIds.flatMap { ids -> Flux.fromIterable(ids) }

        val cityWithDetail = cityIdsFlux.flatMap { cityId ->
            getCityDetail(cityId)
        }

        val cl = CountDownLatch(1)

        cityWithDetail.subscribe({ l ->
            logger.info(l.toString())
        }, { t ->
            t.printStackTrace()
            cl.countDown()
        }, {
            cl.countDown()
        })

        cl.await()

    }

    private fun getCityDetail(cityId: Long): Mono<City> {
        return webClient.get()
                .uri("/cities/{id}", mapOf("id" to cityId))
                .exchange()
                .flatMap { response: ClientResponse ->
                    response.bodyToMono<City>()
                }
    }
}