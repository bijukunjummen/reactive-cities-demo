package samples.geo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl.asyncHttpClient
import org.junit.Ignore
import org.junit.Test
import samples.geo.domain.City
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors.toList

@Ignore
class CitiesNingCompletableFutureKotlinTest {
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val asyncHttpClient: AsyncHttpClient = asyncHttpClient()

    @Test
    fun cityDetails() {
        val cityIdsFuture: CompletableFuture<List<Long>> = asyncHttpClient
                .prepareGet("http://localhost:9090/cityids")
                .execute()
                .toCompletableFuture()
                .thenApply { response ->
                    val s = response.responseBody
                    val l = objectMapper.readValue<List<Long>>(s)
                    l
                }

        val citiesCompletable: CompletableFuture<List<City>> = cityIdsFuture.thenCompose { l ->
            val citiesCompletable: List<CompletableFuture<City>> = l.stream().map { cityId ->
                getCityDetail(cityId)
            }.collect(toList())

            val citiesCompletableArray: Array<CompletableFuture<City>> = citiesCompletable.toTypedArray()
           
            val list = CompletableFuture.allOf(*citiesCompletableArray).thenApply({ _ ->
                citiesCompletable.stream()
                        .map(CompletableFuture<City>::join)
                        .collect(toList())
            })

            list
        }

        println(citiesCompletable.join())
    }

    private fun getCityDetail(cityId: Long): CompletableFuture<City> {
        return asyncHttpClient.prepareGet("http://localhost:9090/cities/" + cityId)
                .execute()
                .toCompletableFuture()
                .thenApply { response ->
                    val s = response.responseBody
                    val city = objectMapper.readValue<City>(s)
                    city
                }
    }
}