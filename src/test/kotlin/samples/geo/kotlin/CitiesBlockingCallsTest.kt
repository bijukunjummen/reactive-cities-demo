package samples.geo.kotlin

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import samples.geo.domain.City
import java.util.stream.Collectors

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CitiesBlockingCallsTest {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @LocalServerPort
    private var localServerPort: Int = 0

    @Test
    fun testGetCitiesBlocking() {
        val cityIds: List<String> = getCityIds()
        val cities: List<City> = cityIds
                .stream()
                .map<City> { cityId -> getCityForId(cityId) }
                .collect(Collectors.toList())

        cities.forEach { city -> LOGGER.info(city.toString()) }
    }

    private fun getCityIds(): List<String> {
        val cityIdsEntity: ResponseEntity<List<String>> = restTemplate
                .exchange("http://localhost:$localServerPort/cityids",
                        HttpMethod.GET,
                        null,
                        object : ParameterizedTypeReference<List<String>>() {})
        return cityIdsEntity.body!!
    }

    private fun getCityForId(id: String): City {
        return restTemplate.getForObject("http://localhost:$localServerPort/cities/$id", City::class.java)!!
    }

    @TestConfiguration
    class SpringConfig {
        @Bean
        fun restTemplate(): RestTemplate {
            return RestTemplate()
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CitiesBlockingCallsTest::class.java)
    }
}
