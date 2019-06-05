package samples.geo.kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.ListenableFuture
import org.asynchttpclient.Response
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import samples.geo.domain.City
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.stream.Collectors

import org.asynchttpclient.Dsl.asyncHttpClient


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CitiesNingCallbackTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private val asyncHttpClient = asyncHttpClient()
    private val executor = Executors.newWorkStealingPool()

    @LocalServerPort
    private val localServerPort: Int? = null

    @Test
    @Throws(Exception::class)
    fun callbackHellTest() {
        val responseListenableFuture: ListenableFuture<Response> = asyncHttpClient
                .prepareGet("http://localhost:$localServerPort/cityids")
                .execute()

        responseListenableFuture.addListener(Runnable {
            try {
                val response: Response = responseListenableFuture.get()
                val responseBody: String = response.responseBody
                val cityIds: List<Long> = objectMapper.readValue<List<Long>>(responseBody,
                        object : TypeReference<List<Long>>() {})

                cityIds.stream().map { cityId ->
                    val cityListenableFuture = asyncHttpClient
                            .prepareGet("http://localhost:$localServerPort/cities/$cityId")
                            .execute()

                    cityListenableFuture.addListener(Runnable {
                        try {
                            val cityDescResp = cityListenableFuture.get()
                            val cityDesc = cityDescResp.responseBody
                            val city = objectMapper.readValue(cityDesc, City::class.java)
                            LOGGER.info("Got city: $city")
                        } catch (e: Exception) {
                            throw RuntimeException(e)
                        }
                    }, executor)
                }.collect(Collectors.toList())
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }, executor)

        Thread.sleep(3000)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CitiesNingCallbackTest::class.java)
    }
}

