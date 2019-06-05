package samples.geo.kotlin

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import samples.geo.domain.City
import java.util.concurrent.CountDownLatch

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CitiesReactorTest {

    @LocalServerPort
    private var localServerPort: Int = 0

    private lateinit var webClient: WebClient

    private fun getCityIds(): Flux<Long> {
        return webClient.get()
                .uri("/cityids")
                .exchange()
                .flatMapMany { response ->
                    LOGGER.info("Received cities..")
                    response.bodyToFlux(Long::class.java)
                }
    }

    @BeforeEach
    fun beforeEach() {
        webClient = WebClient.builder().baseUrl("http://localhost:$localServerPort").build()
    }

    @Test
    fun testGetCities() {
        val cityIdsFlux: Flux<Long> = getCityIds()
        val citiesFlux: Flux<City> = cityIdsFlux
                .flatMap { this.getCityDetail(it) }

        val cl = CountDownLatch(1)

        citiesFlux
                .subscribe({ l -> LOGGER.info(l.toString()) },
                        { t ->
                            t.printStackTrace()
                            cl.countDown()
                        },
                        { cl.countDown() })

        cl.await()
    }

    private fun getCityDetail(cityId: Long?): Mono<City> {
        return webClient.get()
                .uri("/cities/{id}", cityId!!)
                .exchange()
                .flatMap { response ->
                    val city: Mono<City> = response.bodyToMono(City::class.java)
                    LOGGER.info("Received city..")
                    city
                }
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(CitiesReactorTest::class.java)
    }
}
