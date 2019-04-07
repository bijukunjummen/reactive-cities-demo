package samples.geo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import samples.geo.domain.City;

public interface CityRepo extends JpaRepository<City, Long> {
}
