package samples.geo.cities;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import samples.geo.domain.City;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CitiesBlockingCallsTest {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(CitiesBlockingCallsTest.class);

    @LocalServerPort
    private Integer localServerPort;

    @Test
    public void testGetCitiesBlocking() {
        List<String> cityIds = getCityIds();
        List<City> cities = cityIds
                .stream()
                .map(cityId -> getCityForId(cityId))
                .collect(Collectors.toList());

        cities.forEach(city -> LOGGER.info(city.toString()));
    }

    private List<String> getCityIds() {
        ResponseEntity<List<String>> cityIdsEntity = restTemplate
                .exchange(String.format("http://localhost:%d/cityids", localServerPort), HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<String>>() {});
        return cityIdsEntity.getBody();
    }

    private City getCityForId(String id) {
        City city = restTemplate.getForObject(String.format("http://localhost:%d/cities/", localServerPort) + id,
                City.class);
        return city;
    }

    @TestConfiguration
    public static class SpringConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}
