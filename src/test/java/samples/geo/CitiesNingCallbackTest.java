package samples.geo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samples.geo.domain.City;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.asynchttpclient.Dsl.asyncHttpClient;


@Disabled
public class CitiesNingCallbackTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CitiesNingCallbackTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private AsyncHttpClient asyncHttpClient = asyncHttpClient();
    private Executor executor = Executors.newWorkStealingPool();

    @Test
    public void callbackHellTest() throws Exception {
        ListenableFuture<Response> responseListenableFuture = asyncHttpClient
                .prepareGet("http://localhost:9090/cityids")
                .execute();

        responseListenableFuture.addListener(() -> {
            try {
                Response response = responseListenableFuture.get();
                String responseBody = response.getResponseBody();
                List<Long> cityIds = objectMapper.readValue(responseBody, new TypeReference<List<Long>>() {});

                cityIds.stream().map(cityId -> {
                    ListenableFuture<Response> cityListenableFuture =
                            asyncHttpClient
                                    .prepareGet("http://localhost:9090/cities/" + cityId)
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

