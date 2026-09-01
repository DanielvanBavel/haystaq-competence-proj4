package nl.haystaq.huisjacht.shared.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Echte productieomgevingen antwoorden niet elke keer even snel. Deze filter
 * geeft zoekopdrachten en beeldmateriaal een wisselende vertraging, zodat tests
 * die met vaste wachttijden werken vanzelf door de mand vallen.
 * <p>
 * Uit te zetten met {@code FLAKINESS_ENABLED=false}.
 */
@Component
public class LatencyFilter extends OncePerRequestFilter {

    private final boolean enabled;
    private final int minMillis;
    private final int maxMillis;

    public LatencyFilter(
            @Value("${huisjacht.flakiness.enabled:true}") boolean enabled,
            @Value("${huisjacht.flakiness.min-latency-ms:40}") int minMillis,
            @Value("${huisjacht.flakiness.max-latency-ms:900}") int maxMillis) {
        this.enabled = enabled;
        this.minMillis = minMillis;
        this.maxMillis = Math.max(maxMillis, minMillis + 1);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (enabled) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(minMillis, maxMillis));
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        chain.doFilter(request, response);
    }

    /** Alleen zoeken en media zijn traag; statuswijzigingen en test-support niet. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean slowPath = path.startsWith("/api/listings") || path.startsWith("/api/media");
        return !slowPath || path.contains("/test-support");
    }
}
