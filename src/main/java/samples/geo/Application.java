package samples.geo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import samples.geo.domain.City;
import samples.geo.repo.CityRepo;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}

@Service
class DataPopulator implements ApplicationRunner {

    private final CityRepo cityRepo;

    DataPopulator(CityRepo cityRepo) {
        this.cityRepo = cityRepo;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        cityRepo.save(new City("Portland", "USA", 1_600_000L));
        cityRepo.save(new City("Seattle", "USA", 3_200_000L));
        cityRepo.save(new City("SFO", "USA", 6_400_000L));
        cityRepo.save(new City("LA", "USA", 12_800_000L));
        cityRepo.save(new City("Denver", "USA", 3_000_000L));
        cityRepo.save(new City("Chicago", "USA", 25_600_000L));
        cityRepo.save(new City("NY", "USA", 25_600_000L));
    }
}


