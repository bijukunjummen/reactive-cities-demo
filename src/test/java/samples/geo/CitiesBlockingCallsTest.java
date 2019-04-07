package samples.geo;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import samples.geo.domain.City;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CitiesBlockingCallsTest {

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(CitiesBlockingCallsTest.class);

    @Test
    public void testGetCitiesBlocking() {
        List<String> cityIds = getCityIds();
        List<City> cities = cityIds
                .stream()
                .map(cityId -> getCityForId(cityId))
                .collect(Collectors.toList());

        cities
                .forEach(city -> LOGGER.info(city.toString()));
    }

    List<String> getCityIds() {
        ResponseEntity<String> cityIdsEntity = restTemplate.getForEntity("http://localhost:9090/cityids", String.class);
        try {
            List<String> cityIds = objectMapper.readValue(cityIdsEntity.getBody(), new TypeReference<List<String>>() {
            });
            return cityIds;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    City getCityForId(String id) {
        City city = restTemplate.getForObject("http://localhost:9090/cities/" + id, City.class);
        return city;
    }


    static class CityIds {
        @JsonValue
        List<String> cityIds;
    }

}
