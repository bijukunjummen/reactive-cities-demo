package samples.geo.cities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import samples.geo.domain.City;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.asynchttpclient.Dsl.asyncHttpClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CitiesNingCallbackTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CitiesNingCallbackTest.class);

    @Autowired
    private ObjectMapper objectMapper;
    private AsyncHttpClient asyncHttpClient = asyncHttpClient();
    private Executor executor = Executors.newWorkStealingPool();

    @LocalServerPort
    private Integer localServerPort;

    @Test
    public void callbackHellTest() throws Exception {
        ListenableFuture<Response> responseListenableFuture = asyncHttpClient
                .prepareGet(String.format("http://localhost:%s/cityids", localServerPort))
                .execute();

        responseListenableFuture.addListener(() -> {
            try {
                Response response = responseListenableFuture.get();
                String responseBody = response.getResponseBody();
                List<Long> cityIds = objectMapper.readValue(responseBody, new TypeReference<List<Long>>() {});

                cityIds.stream().map(cityId -> {
                    ListenableFuture<Response> cityListenableFuture =
                            asyncHttpClient
                                    .prepareGet(String.format("http://localhost:%d/cities/%d", localServerPort, cityId))
                                    .execute();

                    return cityListenableFuture.addListener(() -> {
                        try {
                            Response cityDescResp = cityListenableFuture.get();
                            String cityDesc = cityDescResp.getResponseBody();
                            City city = objectMapper.readValue(cityDesc, City.class);
                            LOGGER.info("Got city: " + city);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, executor);
                }).collect(Collectors.toList());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);

        Thread.sleep(3000);
    }
}

