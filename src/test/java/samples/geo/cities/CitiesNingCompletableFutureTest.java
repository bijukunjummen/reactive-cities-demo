package samples.geo.cities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import samples.geo.domain.City;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;
import static org.asynchttpclient.Dsl.asyncHttpClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CitiesNingCompletableFutureTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CitiesNingCompletableFutureTest.class);

    @Autowired
    private ObjectMapper objectMapper;
    private AsyncHttpClient asyncHttpClient = asyncHttpClient();

    @LocalServerPort
    private Integer localServerPort;

    @Test
    public void getCityDetails() {
        CompletableFuture<List<Long>> cityIdsFuture = getCityIds();

        CompletableFuture<List<City>> citiesCompletableFuture = cityIdsFuture.thenCompose(l -> {
            List<CompletableFuture<City>> citiesCompletable =
                    l.stream().map(cityId
                            -> getCityDetail(cityId)).collect(toList());

            CompletableFuture<List<City>> citiesCompletableFutureOfList =
                    CompletableFuture.allOf(citiesCompletable.toArray(new CompletableFuture[citiesCompletable.size()]))
                            .thenApply((Void v) -> {
                                List<City> cityList = citiesCompletable.stream().map(CompletableFuture::join)
                                        .collect(toList());
                                return cityList;
                            });
            return citiesCompletableFutureOfList;
        });

        List<City> cities = citiesCompletableFuture.join();

        cities.forEach(city -> LOGGER.info(city.toString()));

    }

    private CompletableFuture<List<Long>> getCityIds() {
        return asyncHttpClient
                .prepareGet(String.format("http://localhost:%d/cityids", localServerPort))
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    String s = response.getResponseBody();
                    try {
                        List<Long> l = objectMapper.readValue(s, new TypeReference<List<Long>>() {
                        });
                        return l;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    private CompletableFuture<City> getCityDetail(Long cityId) {
        return asyncHttpClient.prepareGet(String.format("http://localhost:%d/cities/%d", localServerPort, cityId))
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    String s = response.getResponseBody();
                    LOGGER.info("Got {}", s);
                    try {
                        City city = objectMapper.readValue(s, City.class);
                        return city;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}

