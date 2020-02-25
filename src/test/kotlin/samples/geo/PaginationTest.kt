package samples.geo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import samples.geo.domain.City
import samples.geo.repo.CityRepo

@DataJpaTest
class PaginationTest {

    @Autowired
    private lateinit var cityRepo: CityRepo

    @Test
    fun paginateUsingExpand() {
        createMockCities()

        val result: Flux<City> =
            Mono
                .fromSupplier { cityRepo.findAll(PageRequest.of(0, 5)) }
                .expand { page ->
                    if (page.hasNext())
                        Mono.fromSupplier { cityRepo.findAll(page.nextPageable()) }
                    else
                        Mono.empty()
                }
                .flatMap { page -> Flux.fromIterable(page.content) }

        result.subscribe(
            { page -> LOGGER.info("City ${page}") },
            { t -> t.printStackTrace() }
        )
    }

    @Test
    fun paginateNoReactor() {
        createMockCities()
        var pageable: Pageable = PageRequest.of(0, 5)
        do {
            var page: Page<City> = cityRepo.findAll(pageable)
            page.content.forEach { city -> LOGGER.info("City $city") }
            pageable = page.nextPageable()
        } while (page.hasNext())
    }

    private fun createMockCities() {
        for (i in 1..20) {
            cityRepo.save(City("name$i", "country$i", i * 100000L))
        }
    }

    companion object {
        val LOGGER = loggerFor<PaginationTest>()
    }
}