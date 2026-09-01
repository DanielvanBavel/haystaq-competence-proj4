package nl.haystaq.huisjacht.ui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UiProfileController {

    private final UiProfileService profiles;

    public UiProfileController(UiProfileService profiles) {
        this.profiles = profiles;
    }

    /** De frontend haalt dit op en bouwt zijn HTML ermee op. */
    @GetMapping("/ui-profile")
    public UiProfileService.UiState current() {
        return profiles.current();
    }

    /** Overzicht van alle releases, zodat je weet wat er nog komt. */
    @GetMapping("/ui-profile/releases")
    public List<Map<String, Object>> releases() {
        return UiProfile.RELEASES.stream()
                .map(profile -> Map.<String, Object>of(
                        "revision", profile.revision(),
                        "name", profile.name(),
                        "summary", profile.summary()))
                .toList();
    }

    @PostMapping("/test-support/ui/release")
    public UiProfileService.UiState release() {
        return profiles.release();
    }

    @PostMapping("/test-support/ui/pin/{revision}")
    public UiProfileService.UiState pin(@PathVariable int revision) {
        return profiles.pin(revision);
    }
}
