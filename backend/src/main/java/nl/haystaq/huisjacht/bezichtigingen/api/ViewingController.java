package nl.haystaq.huisjacht.bezichtigingen.api;

import nl.haystaq.huisjacht.bezichtigingen.application.ViewingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ViewingController {

    private final ViewingService viewings;

    public ViewingController(ViewingService viewings) {
        this.viewings = viewings;
    }

    @GetMapping("/listings/{reference}/slots")
    public List<ViewingService.SlotView> slots(
            @PathVariable String reference,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return viewings.slots(reference, date);
    }

    @PostMapping("/viewings")
    public ResponseEntity<ViewingService.ViewingView> request(
            @RequestBody ViewingService.RequestViewing command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viewings.request(command));
    }

    @GetMapping("/viewings")
    public List<ViewingService.ViewingView> all(@RequestParam(required = false) String listingReference) {
        return listingReference == null ? viewings.all() : viewings.forListing(listingReference);
    }

    public record Decide(String action) {
    }

    @PostMapping("/viewings/{id}/decide")
    public ViewingService.ViewingView decide(@PathVariable UUID id, @RequestBody Decide command) {
        return viewings.decide(id, command.action());
    }
}
