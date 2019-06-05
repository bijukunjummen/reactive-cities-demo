package samples.geo.kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.asynchttpclient.Dsl.asyncHttpClient
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import samples.geo.domain.City
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors.toList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CitiesNingCompletableFutureTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val asyncHttpClient = asyncHttpClient()

    @LocalServerPort
    private val localServerPort: Int? = null

    @Test
    fun getCityDetails() {
        val cityIdsFuture: CompletableFuture<List<Long>> = getCityIds()
        val citiesCompletableFuture: CompletableFuture<List<City>> =
                cityIdsFuture
                        .thenCompose { l ->
                            val citiesCompletable: List<CompletableFuture<City>> =
                                    l.stream()
                                            .map { cityId ->
                                                getCityDetail(cityId)
                                            }.collect(toList())

                            val citiesCompletableFutureOfList: CompletableFuture<List<City>> =
                                    CompletableFuture.allOf(*citiesCompletable.toTypedArray())
                                            .thenApply { _: Void? ->
                                                val cityList: List<City> =
                                                        citiesCompletable
                                                                .stream()
                                                                .map { it.join() }
                                                                .collect(toList())
                                                cityList
                                            }
                            citiesCompletableFutureOfList
                        }

        val cities: List<City> = citiesCompletableFuture.join()
        cities.forEach { city -> LOGGER.info(city.toString()) }
    }

    private fun getCityIds(): CompletableFuture<List<Long>> {
        return asyncHttpClient
                .prepareGet("http://localhost:$localServerPort/cityids")
                .execute()
                .toCompletableFuture()
                .thenApply { response ->
                    val s = response.responseBody
                    val l: List<Long> = objectMapper.readValue(s, object : TypeReference<List<Long>>() {})
                    l
                }
    }

    private fun getCityDetail(cityId: Long): CompletableFuture<City> {
        return asyncHttpClient.prepareGet("http://localhost:$localServerPort/cities/$cityId")
                .execute()
                .toCompletableFuture()
                .thenApply { response ->
                    val s = response.responseBody
                    LOGGER.info("Got {}", s)
                    val city = objectMapper.readValue(s, City::class.java)
                    city
                }
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(CitiesNingCompletableFutureTest::class.java)
    }
}

