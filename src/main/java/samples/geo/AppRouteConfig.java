package samples.geo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import samples.geo.web.CityHandler;

@Configuration
public class AppRouteConfig {

    @Bean
    public RouterFunction<?> routerFunction(CityHandler cityHandler) {
        return AppRoutes.routes(cityHandler);
    }

}
