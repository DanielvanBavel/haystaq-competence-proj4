package nl.haystaq.huisjacht.shared.api;

import org.flywaydb.core.Flyway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Alleen voor testomgevingen: de database terug naar de begintoestand. */
@RestController
@RequestMapping("/api/test-support")
public class TestSupportController {

    private final Flyway flyway;

    public TestSupportController(Flyway flyway) {
        this.flyway = flyway;
    }

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        flyway.clean();
        return Map.of("status", "reset", "migrations", flyway.migrate().migrationsExecuted);
    }
}
