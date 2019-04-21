package samples.geo.cities;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import samples.geo.domain.City;

import java.util.concurrent.CountDownLatch;

@Disabled
public class CitiesReactorTest {

    private WebClient webClient = WebClient.builder().baseUrl("http://localhost:9090").build();
    private static final Logger LOGGER = LoggerFactory.getLogger(CitiesReactorTest.class);

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
