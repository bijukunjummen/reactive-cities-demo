package samples.geo.tc;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import samples.geo.domain.City;
import samples.geo.repo.CityRepo;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(initializers = {CitiesWithPostgresContainerTest.Initializer.class})
public class CitiesWithPostgresContainerTest {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer =
            (PostgreSQLContainer) new PostgreSQLContainer("postgres:10.4")
                    .withDatabaseName("sampledb")
                    .withUsername("sampleuser")
                    .withPassword("samplepwd")
                    .withStartupTimeout(Duration.ofSeconds(600));

    @Autowired
    private CityRepo cityRepo;

    @Test
    public void testWithDb() {
        City city1 = cityRepo.save(new City("city1", "USA", 20000L));
        City city2 = cityRepo.save(new City("city2", "USA", 40000L));

        assertThat(city1)
                .matches(c -> c.getId() != null && c.getName() == "city1" && c.getPop() == 20000L);

        assertThat(city2)
                .matches(c -> c.getId() != null && c.getName() == "city2" && c.getPop() == 40000L);

        assertThat(cityRepo.findAll()).containsExactly(city1, city2);
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}