package samples.geo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import samples.geo.domain.City;

@Service
public class CitiesService {
    private final WebClient.Builder webClientBuilder;
    private final String baseUrl;

    public CitiesService(
            WebClient.Builder webClientBuilder,
            @Value("${cityservice.url}") String baseUrl) {
        this.webClientBuilder = webClientBuilder;
        this.baseUrl = baseUrl;
    }


    public Flux<City> getCities() {
        return this.webClientBuilder.build()
                .get()
                .uri(String.format("%s/cities", baseUrl))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMapMany(resp ->
                        resp.bodyToFlux(City.class));
    }


}
