package nl.haystaq.huisjacht.bezichtigingen.application;

import nl.haystaq.huisjacht.bezichtigingen.domain.ListingDirectory;
import nl.haystaq.huisjacht.bezichtigingen.domain.ViewingRequest;
import nl.haystaq.huisjacht.bezichtigingen.domain.ViewingRequestRepository;
import nl.haystaq.huisjacht.shared.domain.BusinessRuleViolation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ViewingService {

    private static final int MAX_OPEN_PER_LISTING = 2;

    private final ViewingRequestRepository requests;
    private final ListingDirectory listings;

    public ViewingService(ViewingRequestRepository requests, ListingDirectory listings) {
        this.requests = requests;
        this.listings = listings;
    }

    public record RequestViewing(String listingReference, String name, String email, String phone,
                                 LocalDate date, String slot, String message) {
    }

    public record ViewingView(UUID id, String listingReference, String listingAddress, String name, String email,
                              String phone, LocalDate date, String slot, String message, String status,
                              OffsetDateTime createdAt) {
    }

    public record SlotView(String slot, boolean available, String reason) {
    }

    /**
     * Beschikbare tijdvakken voor een dag. Voor vandaag vervallen de vakken die al
     * begonnen zijn - de uitkomst hangt dus af van het moment waarop je het vraagt.
     */
    @Transactional(readOnly = true)
    public List<SlotView> slots(String listingReference, LocalDate date) {
        ListingDirectory.ListingSnapshot listing = requireListing(listingReference);
        List<String> taken = requests.takenSlots(listing.id(), date);
        LocalTime now = LocalTime.now();
        boolean today = date.equals(LocalDate.now());

        return ViewingRequest.SLOTS.stream()
                .map(slot -> {
                    if (taken.contains(slot)) {
                        return new SlotView(slot, false, "al gereserveerd");
                    }
                    LocalTime start = LocalTime.parse(slot.substring(0, 5));
                    if (today && !start.isAfter(now)) {
                        return new SlotView(slot, false, "al begonnen");
                    }
                    return new SlotView(slot, true, null);
                })
                .toList();
    }

    public ViewingView request(RequestViewing command) {
        ListingDirectory.ListingSnapshot listing = requireListing(command.listingReference());
        BusinessRuleViolation.requireState(listing.acceptsViewings(), "listing.not_viewable",
                "Voor deze woning kun je geen bezichtiging meer aanvragen.");

        ViewingRequest request = ViewingRequest.request(listing.id(), command.name(), command.email(),
                command.phone(), command.date(), command.slot(), command.message(), LocalDate.now());

        BusinessRuleViolation.requireState(
                requests.countOpenFor(listing.id(), request.email()) < MAX_OPEN_PER_LISTING,
                "viewing.too_many_open",
                "Je hebt al twee openstaande aanvragen voor deze woning.");
        BusinessRuleViolation.requireState(
                !requests.takenSlots(listing.id(), command.date()).contains(command.slot()),
                "viewing.slot_taken",
                "Dit tijdvak is inmiddels gereserveerd. Kies een ander moment.");

        return view(requests.save(request), listing);
    }

    public ViewingView decide(UUID id, String action) {
        ViewingRequest request = requests.findById(id)
                .orElseThrow(() -> BusinessRuleViolation.notFound("viewing.not_found", "Onbekende aanvraag."));
        switch (action == null ? "" : action.toLowerCase()) {
            case "bevestigen" -> request.confirm();
            case "afwijzen" -> request.reject();
            case "annuleren" -> request.cancel();
            default -> throw BusinessRuleViolation.invalid("viewing.action_unknown",
                    "Onbekende actie: " + action);
        }
        requests.save(request);
        return view(request, listings.find(request.listingId()).orElse(null));
    }

    @Transactional(readOnly = true)
    public List<ViewingView> all() {
        return requests.findAll().stream()
                .map(request -> view(request, listings.find(request.listingId()).orElse(null)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ViewingView> forListing(String listingReference) {
        ListingDirectory.ListingSnapshot listing = requireListing(listingReference);
        return requests.findByListing(listing.id()).stream()
                .map(request -> view(request, listing))
                .toList();
    }

    private ListingDirectory.ListingSnapshot requireListing(String reference) {
        return listings.findByReference(reference)
                .orElseThrow(() -> BusinessRuleViolation.notFound("listing.not_found",
                        "Deze woning staat niet (meer) op HuisJacht."));
    }

    private static ViewingView view(ViewingRequest request, ListingDirectory.ListingSnapshot listing) {
        return new ViewingView(
                request.id(),
                listing == null ? null : listing.reference(),
                listing == null ? null : listing.address(),
                request.requesterName(),
                request.email(),
                request.phone(),
                request.preferredDate(),
                request.preferredSlot(),
                request.message(),
                request.status().name(),
                request.createdAt());
    }
}
