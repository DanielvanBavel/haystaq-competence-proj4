package nl.haystaq.huisjacht.aanbod.api;

import nl.haystaq.huisjacht.aanbod.domain.ListingPhoto;
import nl.haystaq.huisjacht.aanbod.domain.ListingRepository;
import nl.haystaq.huisjacht.shared.domain.BusinessRuleViolation;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;

/**
 * Alle beeldmateriaal wordt hier gegenereerd. Zo heeft de applicatie honderden
 * foto's zonder dat er een byte aan externe media in de repository staat.
 */
@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final ListingRepository listings;

    public MediaController(ListingRepository listings) {
        this.listings = listings;
    }

    @GetMapping(value = "/photos/{id}", produces = "image/svg+xml")
    public ResponseEntity<String> photo(@PathVariable UUID id,
                                        @RequestParam(defaultValue = "1200") int width) {
        ListingPhoto photo = listings.findPhoto(id)
                .orElseThrow(() -> BusinessRuleViolation.notFound("photo.not_found", "Foto bestaat niet."));
        int height = Math.round(width * 0.66f);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)))
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(render(photo, width, height));
    }

    private static String render(ListingPhoto photo, int width, int height) {
        int hue = photo.hue();
        String sky = "hsl(%d, 45%%, 78%%)".formatted((hue + 200) % 360);
        String skyLow = "hsl(%d, 40%%, 92%%)".formatted((hue + 200) % 360);
        String wall = "hsl(%d, 32%%, %d%%)".formatted(hue, 62 - (photo.position() % 4) * 6);
        String roof = "hsl(%d, 38%%, 34%%)".formatted((hue + 20) % 360);
        String accent = "hsl(%d, 55%%, 45%%)".formatted((hue + 180) % 360);
        String ground = "hsl(%d, 30%%, 46%%)".formatted((hue + 110) % 360);

        int windowRows = 2 + (photo.position() % 3);
        StringBuilder windows = new StringBuilder();
        for (int row = 0; row < windowRows; row++) {
            for (int column = 0; column < 3; column++) {
                int x = 300 + column * 190;
                int y = 260 + row * 150;
                windows.append("""
                        <rect x="%d" y="%d" width="130" height="100" rx="6" fill="%s" opacity="0.85"/>
                        <line x1="%d" y1="%d" x2="%d" y2="%d" stroke="%s" stroke-width="4"/>
                        """.formatted(x, y, skyLow, x + 65, y, x + 65, y + 100, wall));
            }
        }

        return """
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 800" width="%d" height="%d"
                     role="img" aria-label="%s">
                  <defs>
                    <linearGradient id="lucht" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%%" stop-color="%s"/>
                      <stop offset="100%%" stop-color="%s"/>
                    </linearGradient>
                  </defs>
                  <rect width="1200" height="800" fill="url(#lucht)"/>
                  <rect y="640" width="1200" height="160" fill="%s"/>
                  <polygon points="600,90 1010,300 190,300" fill="%s"/>
                  <rect x="230" y="300" width="740" height="360" fill="%s"/>
                  %s
                  <rect x="540" y="470" width="120" height="190" rx="4" fill="%s"/>
                  <circle cx="645" cy="565" r="7" fill="%s"/>
                  <text x="60" y="740" font-family="Segoe UI, sans-serif" font-size="42" fill="#ffffff"
                        opacity="0.92">%s</text>
                  <text x="60" y="782" font-family="Segoe UI, sans-serif" font-size="26" fill="#ffffff"
                        opacity="0.75">%s</text>
                </svg>
                """.formatted(width, height, escape(photo.caption()), sky, skyLow, ground, roof, wall,
                windows, roof, accent, escape(photo.room()), escape(photo.caption()));
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
