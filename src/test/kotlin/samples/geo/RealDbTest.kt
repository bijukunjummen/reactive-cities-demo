package samples.geo

import org.assertj.core.api.Assertions.assertThat
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import samples.geo.domain.City
import samples.geo.repo.CityRepo
import java.time.Duration

@RunWith(SpringRunner::class)
@DataJpaTest
@ContextConfiguration(initializers = [RealDbTest.Initializer::class])
class RealDbTest {

    @Autowired
    private lateinit var cityRepo: CityRepo

    @Test
    fun testCitiesWithPostgres() {
        cityRepo.save(City("Portland", "USA", 16000000L))
        cityRepo.save(City("Seattle", "USA", 32000000L))
        assertThat(cityRepo.findAll()).hasSize(2)
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        
        @JvmField
        @ClassRule
        val postgreSQLContainer = KPostgresContainer("postgres:10.3")
                .withDatabaseName("sampledb")
                .withUsername("sampleuser")
                .withPassword("samplepwd")
                .withStartupTimeout(Duration.ofSeconds(600))
        
    }
}