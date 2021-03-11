package samples.geo;

import org.springframework.web.reactive.function.server.RouterFunction;
import samples.geo.web.CityHandler;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class AppRoutes {
    public static RouterFunction<?> routes(CityHandler cityHandler) {
        return nest(path("/cities")
                        .and(accept(APPLICATION_JSON)),
                route(GET("/{id}"), cityHandler::getCity)
                        .and(route(method(POST), cityHandler::createCity))
                        .and(route(method(GET), cityHandler::getCities))
        ).and(nest(path("/cityids")
                        .and(accept(APPLICATION_JSON)),
                route(method(GET), cityHandler::getCityIds)));
    }

}
