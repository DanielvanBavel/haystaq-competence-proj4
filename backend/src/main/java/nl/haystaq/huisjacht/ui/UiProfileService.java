package nl.haystaq.huisjacht.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Houdt bij welke UI-revisie live staat en zet er periodiek een nieuwe op, alsof
 * er elk kwartier een release wordt uitgerold.
 */
@Service
public class UiProfileService {

    private static final Logger log = LoggerFactory.getLogger(UiProfileService.class);

    private final AtomicInteger revision;
    private final boolean driftEnabled;
    private final long intervalMinutes;
    private final boolean flakinessEnabled;
    private volatile Instant releasedAt = Instant.now();

    public UiProfileService(
            @Value("${huisjacht.ui-drift.enabled:true}") boolean driftEnabled,
            @Value("${huisjacht.ui-drift.interval-minutes:15}") long intervalMinutes,
            @Value("${huisjacht.ui-drift.start-revision:1}") int startRevision,
            @Value("${huisjacht.flakiness.enabled:true}") boolean flakinessEnabled) {
        this.driftEnabled = driftEnabled;
        this.intervalMinutes = intervalMinutes;
        this.flakinessEnabled = flakinessEnabled;
        this.revision = new AtomicInteger(clamp(startRevision));
        log.info("UI-drift {}, interval {} minuten, start op revisie {}",
                driftEnabled ? "aan" : "uit", intervalMinutes, this.revision.get());
    }

    public record UiState(
            int revision,
            String name,
            String summary,
            java.util.Map<String, String> tokens,
            java.util.List<String> filterOrder,
            boolean driftEnabled,
            long intervalMinutes,
            String releasedAt,
            String nextReleaseAt,
            int totalRevisions,
            boolean flakinessEnabled) {
    }

    public UiState current() {
        UiProfile profile = UiProfile.byRevision(revision.get());
        Instant next = driftEnabled ? releasedAt.plus(Duration.ofMinutes(intervalMinutes)) : null;
        return new UiState(
                profile.revision(),
                profile.name(),
                profile.summary(),
                profile.tokens(),
                profile.filterOrder(),
                driftEnabled,
                intervalMinutes,
                releasedAt.toString(),
                next == null ? null : next.toString(),
                UiProfile.RELEASES.size(),
                flakinessEnabled);
    }

    /** Rolt de volgende revisie uit; na de laatste begint hij weer bij één. */
    public UiState release() {
        int next = revision.updateAndGet(current -> current >= UiProfile.RELEASES.size() ? 1 : current + 1);
        releasedAt = Instant.now();
        log.warn("nieuwe UI-revisie live: {} ({})", next, UiProfile.byRevision(next).name());
        return current();
    }

    /** Zet de UI vast op een specifieke revisie. Handig tijdens een demo. */
    public UiState pin(int wanted) {
        revision.set(clamp(wanted));
        releasedAt = Instant.now();
        log.warn("UI-revisie vastgezet op {}", revision.get());
        return current();
    }

    @Scheduled(fixedDelayString = "${huisjacht.ui-drift.interval-minutes:15}m",
            initialDelayString = "${huisjacht.ui-drift.interval-minutes:15}m")
    void scheduledRelease() {
        if (!driftEnabled) {
            return;
        }
        release();
    }

    private static int clamp(int value) {
        if (value < 1) {
            return 1;
        }
        return Math.min(value, UiProfile.RELEASES.size());
    }
}
