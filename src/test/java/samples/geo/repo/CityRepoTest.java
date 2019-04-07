package samples.geo.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import samples.geo.domain.City;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CityRepoTest {

    @Autowired
    private CityRepo cityRepo;

    @Test
    public void testCreateACity() {
        var city = new City("city1", "USA", 10_000L);

        var savedCity = cityRepo.save(city);

        assertThat(savedCity).isEqualTo(city);
        assertThat(cityRepo.getOne(city.getId())).isEqualTo(city);
    }
}
