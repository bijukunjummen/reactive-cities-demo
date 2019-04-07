package samples.geo;

import org.springframework.web.reactive.function.server.RouterFunction;
import samples.geo.web.CityHandler;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class AppRoutes {
    public static RouterFunction<?> routes(CityHandler cityHandler) {
        return
                nest(
                        path("/cities")
                                .and(accept(APPLICATION_JSON)),
                        route(GET("/"), cityHandler::getCities)
                                .and(route(GET("/{id}"), cityHandler::getCity))
                                .and(route(POST("/"), cityHandler::createCity))
                ).and(
                        nest(
                                path("/cityids")
                                        .and(accept(APPLICATION_JSON)),
                                route(GET("/"), cityHandler::getCityIds)));
    }

}
