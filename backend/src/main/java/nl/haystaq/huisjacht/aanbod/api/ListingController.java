package nl.haystaq.huisjacht.aanbod.api;

import nl.haystaq.huisjacht.aanbod.application.ListingService;
import nl.haystaq.huisjacht.aanbod.application.ListingViews;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ListingController {

    private final ListingService listings;

    public ListingController(ListingService listings) {
        this.listings = listings;
    }

    @GetMapping("/listings")
    public ListingViews.SearchResult search(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minRooms,
            @RequestParam(required = false) Integer minLivingArea,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Boolean garden,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int pageSize) {
        return listings.search(new ListingService.SearchQuery(term, city, minPrice, maxPrice, minRooms,
                minLivingArea, propertyType, garden, status, sort, page, pageSize));
    }

    @GetMapping("/listings/{reference}")
    public ListingViews.DetailView detail(@PathVariable String reference) {
        return listings.byReference(reference);
    }

    @GetMapping("/cities")
    public List<String> cities() {
        return listings.cities();
    }

    public record ChangeStatus(String status) {
    }

    @PostMapping("/listings/{id}/status")
    public ListingViews.SummaryView changeStatus(@PathVariable UUID id, @RequestBody ChangeStatus command) {
        return listings.changeStatus(id, command.status());
    }

    @GetMapping("/property-types")
    public List<Map<String, String>> propertyTypes() {
        return List.of(
                Map.of("value", "APPARTEMENT", "label", "Appartement"),
                Map.of("value", "TUSSENWONING", "label", "Tussenwoning"),
                Map.of("value", "HOEKWONING", "label", "Hoekwoning"),
                Map.of("value", "TWEE_ONDER_EEN_KAP", "label", "Twee-onder-een-kap"),
                Map.of("value", "VRIJSTAAND", "label", "Vrijstaand"),
                Map.of("value", "BENEDENWONING", "label", "Benedenwoning"));
    }
}
