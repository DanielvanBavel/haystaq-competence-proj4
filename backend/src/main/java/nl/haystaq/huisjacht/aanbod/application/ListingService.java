package nl.haystaq.huisjacht.aanbod.application;

import nl.haystaq.huisjacht.aanbod.domain.AgencyDirectory;
import nl.haystaq.huisjacht.aanbod.domain.Listing;
import nl.haystaq.huisjacht.aanbod.domain.ListingPhoto;
import nl.haystaq.huisjacht.aanbod.domain.ListingRepository;
import nl.haystaq.huisjacht.aanbod.infrastructure.ListingSpringDataRepository;
import nl.haystaq.huisjacht.shared.domain.BusinessRuleViolation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListingService {

    private final ListingRepository listings;
    private final ListingSpringDataRepository photoQueries;
    private final AgencyDirectory agencies;

    public ListingService(ListingRepository listings, ListingSpringDataRepository photoQueries,
                          AgencyDirectory agencies) {
        this.listings = listings;
        this.photoQueries = photoQueries;
        this.agencies = agencies;
    }

    public record SearchQuery(
            String term,
            String city,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minRooms,
            Integer minLivingArea,
            String propertyType,
            Boolean garden,
            String status,
            String sort,
            int page,
            int pageSize) {
    }

    public ListingViews.SearchResult search(SearchQuery query) {
        String propertyType = Listing.PropertyType.parse(query.propertyType()) == null
                ? null
                : Listing.PropertyType.parse(query.propertyType()).name();

        List<Listing> found = listings.search(
                blankToNull(query.city()),
                query.minPrice(),
                query.maxPrice(),
                query.minRooms(),
                query.minLivingArea(),
                propertyType,
                query.garden(),
                blankToNull(query.status()),
                blankToNull(query.term()));

        List<Listing> sorted = sort(found, query.sort());

        int pageSize = query.pageSize() <= 0 ? 12 : Math.min(query.pageSize(), 48);
        int page = Math.max(0, query.page());
        int from = Math.min(page * pageSize, sorted.size());
        int to = Math.min(from + pageSize, sorted.size());

        List<ListingViews.SummaryView> results = sorted.subList(from, to).stream()
                .map(this::summary)
                .toList();

        int totalPages = (int) Math.ceil(sorted.size() / (double) pageSize);
        return new ListingViews.SearchResult(results, sorted.size(), page, pageSize, Math.max(totalPages, 1));
    }

    public ListingViews.DetailView byReference(String reference) {
        Listing listing = listings.findByReference(reference)
                .orElseThrow(() -> BusinessRuleViolation.notFound("listing.not_found",
                        "Deze woning staat niet (meer) op HuisJacht."));
        List<ListingPhoto> photos = photoQueries.findPhotosOf(listing.id());
        return ListingViews.DetailView.of(listing, photos, agencies.find(listing.agencyId()).orElse(null));
    }

    public List<String> cities() {
        return listings.cities();
    }

    @Transactional
    public ListingViews.SummaryView changeStatus(UUID listingId, String status) {
        Listing listing = listings.findById(listingId)
                .orElseThrow(() -> BusinessRuleViolation.notFound("listing.not_found", "Onbekende woning."));
        Listing.Status next;
        try {
            next = Listing.Status.valueOf(status.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw BusinessRuleViolation.invalid("listing.status_unknown", "Onbekende status: " + status);
        }
        listing.changeStatus(next);
        return summary(listings.save(listing));
    }

    private ListingViews.SummaryView summary(Listing listing) {
        List<ListingPhoto> photos = photoQueries.findPhotosOf(listing.id());
        return ListingViews.SummaryView.of(listing, photos.size(),
                photos.isEmpty() ? null : photos.get(0).id());
    }

    private static List<Listing> sort(List<Listing> found, String sort) {
        Comparator<Listing> comparator = switch (sort == null ? "" : sort) {
            case "prijs-oplopend" -> Comparator.comparing(Listing::price);
            case "prijs-aflopend" -> Comparator.comparing(Listing::price).reversed();
            case "oppervlakte" -> Comparator.comparingInt(Listing::livingAreaM2).reversed();
            default -> Comparator.comparing(Listing::publishedAt).reversed();
        };
        return found.stream().sorted(comparator).toList();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
