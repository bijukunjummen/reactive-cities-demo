package samples.geo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import samples.geo.AppRoutes;
import samples.geo.domain.City;
import samples.geo.repo.CityRepo;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

public class CityHandlerTests {
    private WebTestClient webTestClient;
    private CityRepo cityRepo = mock(CityRepo.class);

    @BeforeEach
    public void setUp() {
        when(cityRepo.findAll())
                .thenReturn(List.of(
                        new City(1L, "test1", "country1", 1L),
                        new City(2L, "test2", "country2", 2L)
                ));

        final CityHandler cityHandler = new CityHandler(cityRepo);

        this.webTestClient = WebTestClient.bindToRouterFunction(AppRoutes.routes(cityHandler)).build();
    }

    @Test
    public void getAllCities() {
        webTestClient.get()
                .uri("/cities")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(City.class)
                .hasSize(2)
                .contains(
                        new City(1L, "test1", "country1", 1L),
                        new City(2L, "test2", "country2", 2L)
                );
    }

    @Test
    public void getAllCityIds() {
        webTestClient.get()
                .uri("/cityids")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Long.class)
                .contains(1L, 2L);
    }

    @Test
    public void saveCity() {
        when(cityRepo.save(any(City.class))).thenAnswer( invocation -> invocation.getArgument(0));


        webTestClient.post()
                .uri("/cities")
                .body(fromObject(new City("test1", "country1", 1L)))
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
    }

    @Test
    public void getCity() {
        when(cityRepo.findById(1L)).thenReturn(Optional.of(new City(1L, "test1", "country1", 1L)));
        when(cityRepo.findById(2L)).thenReturn(Optional.of(new City(2L, "test2", "country2", 2L)));

        webTestClient.get()
                .uri("/cities/1")
                .exchange()
                .expectBody()
                .json("{\"id\": 1, \"name\": \"test1\",\"country\":\"country1\",\"pop\": 1}");
    }
}
