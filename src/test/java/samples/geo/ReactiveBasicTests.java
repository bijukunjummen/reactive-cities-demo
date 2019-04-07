package samples.geo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReactiveBasicTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveBasicTests.class);

    @Test
    public void testBasics() throws InterruptedException {
        Flux<String> flux = Flux.just(
                "biju", "jagadevi", "ignacio", "chris", "loren"
        ).delayElements(Duration.ofSeconds(1));

        CountDownLatch latch = new CountDownLatch(1);
        flux.subscribe(str -> {
            LOGGER.info(str);
        }, t -> {
            LOGGER.error(t.getMessage(), t);
            latch.countDown();
        }, () -> {
            LOGGER.info("DONE!!");
            latch.countDown();
        }, s -> s.request(2));

        latch.await(10, TimeUnit.SECONDS);
    }
}
