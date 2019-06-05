package samples.geo.cities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import samples.geo.domain.City;

import java.util.concurrent.CountDownLatch;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CitiesReactorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CitiesReactorTest.class);

    @LocalServerPort
    private Integer localServerPort;

    private WebClient webClient;

    @BeforeEach
    public void beforeEach() {
        webClient = WebClient.builder().baseUrl(String.format("http://localhost:%d", localServerPort)).build();
    }

    @Test
    public void testGetCities() throws Exception {
        Flux<Long> cityIdsFlux = getCityIds();
        Flux<City> citiesFlux = cityIdsFlux
                .flatMap(this::getCityDetail);

        CountDownLatch cl = new CountDownLatch(1);

        citiesFlux
                .subscribe(l -> LOGGER.info(l.toString()),
                        t -> {
                            t.printStackTrace();
                            cl.countDown();
                        },
                        () -> cl.countDown());

        cl.await();
    }

    private Flux<Long> getCityIds() {
        return webClient.get()
                .uri("/cityids")
                .exchange()
                .flatMapMany(response -> {
                    LOGGER.info("Received cities..");
                    return response.bodyToFlux(Long.class);
                });

    }

    private Mono<City> getCityDetail(Long cityId) {
        return webClient.get()
                .uri("/cities/{id}", cityId)
                .exchange()
                .flatMap(response -> {
                    Mono<City> city = response.bodyToMono(City.class);
                    LOGGER.info("Received city..");
                    return city;
                });
    }
}
