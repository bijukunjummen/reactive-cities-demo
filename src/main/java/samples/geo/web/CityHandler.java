package samples.geo.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import samples.geo.domain.City;
import samples.geo.repo.CityRepo;

import java.util.Optional;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Service
public class CityHandler {
    private final CityRepo cityRepo;

    public CityHandler(CityRepo cityRepo) {
        this.cityRepo = cityRepo;
    }

    public Mono<ServerResponse> getCities(ServerRequest request) {
        var cities = cityRepo.findAll();
        return ServerResponse.ok().body(fromObject(cities));
    }

    public Mono<ServerResponse> getCity(ServerRequest request) {
        var id = Long.valueOf(request.pathVariable("id"));
        Optional<City> cityOptional = cityRepo.findById(id);

        return cityOptional
                .map(city -> ServerResponse.ok().body(fromObject(city)))
                .orElse(ServerResponse
                        .notFound()
                        .build());
    }

    public Mono<ServerResponse> createCity(ServerRequest request) {
        return request.bodyToMono(City.class)
                .map(city -> cityRepo.save(city))
                .flatMap(city ->
                        ServerResponse.status(HttpStatus.CREATED).body(fromObject(city)));
    }

    public Mono<ServerResponse> getCityIds(ServerRequest request) {
        return Flux.fromIterable(cityRepo.findAll())
                .map(city -> city.getId())
                .collectList()
                .flatMap(list -> ServerResponse.ok().body(fromObject(list)));
    }
}