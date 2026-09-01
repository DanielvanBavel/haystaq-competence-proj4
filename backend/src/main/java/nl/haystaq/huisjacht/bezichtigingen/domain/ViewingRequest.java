package nl.haystaq.huisjacht.bezichtigingen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import nl.haystaq.huisjacht.shared.domain.BusinessRuleViolation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/** Aggregate root van het context bezichtigingen. */
@Entity
@Table(name = "viewing_request")
public class ViewingRequest {

    /** De vaste tijdvakken waarin makelaars rondleiden. */
    public static final List<String> SLOTS = List.of("09:00-10:00", "10:00-11:00", "11:00-12:00",
            "13:00-14:00", "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00");

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s.]+\\.[^@\\s]{2,}$");
    private static final int MAX_DAYS_AHEAD = 30;

    public enum Status {
        AANGEVRAAGD,
        BEVESTIGD,
        AFGEWEZEN,
        GEANNULEERD
    }

    @Id
    private UUID id;

    @Column(name = "listing_id", nullable = false)
    private UUID listingId;

    @Column(name = "requester_name", nullable = false)
    private String requesterName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    @Column(name = "preferred_slot", nullable = false)
    private String preferredSlot;

    @Column(name = "message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected ViewingRequest() {
        // voor JPA
    }

    public static ViewingRequest request(UUID listingId, String requesterName, String email, String phone,
                                         LocalDate preferredDate, String preferredSlot, String message,
                                         LocalDate today) {
        BusinessRuleViolation.require(requesterName != null && requesterName.trim().length() >= 2,
                "viewing.name_required", "Vul je naam in.");
        BusinessRuleViolation.require(email != null && EMAIL.matcher(email).matches(),
                "viewing.email_invalid", "Vul een geldig e-mailadres in.");
        BusinessRuleViolation.require(preferredDate != null, "viewing.date_required", "Kies een datum.");
        BusinessRuleViolation.require(!preferredDate.isBefore(today), "viewing.date_in_past",
                "Kies een datum in de toekomst.");
        BusinessRuleViolation.require(ChronoUnit.DAYS.between(today, preferredDate) <= MAX_DAYS_AHEAD,
                "viewing.date_too_far", "Je kunt maximaal 30 dagen vooruit plannen.");
        BusinessRuleViolation.require(preferredDate.getDayOfWeek() != DayOfWeek.SUNDAY,
                "viewing.sunday", "Op zondag wordt er niet bezichtigd.");
        BusinessRuleViolation.require(SLOTS.contains(preferredSlot), "viewing.slot_unknown",
                "Kies een van de beschikbare tijdvakken.");

        ViewingRequest request = new ViewingRequest();
        request.id = UUID.randomUUID();
        request.listingId = listingId;
        request.requesterName = requesterName.trim();
        request.email = email.trim().toLowerCase(Locale.ROOT);
        request.phone = phone == null || phone.isBlank() ? null : phone.trim();
        request.preferredDate = preferredDate;
        request.preferredSlot = preferredSlot;
        request.message = message == null || message.isBlank() ? null : message.trim();
        request.status = Status.AANGEVRAAGD;
        return request;
    }

    public void confirm() {
        BusinessRuleViolation.requireState(status == Status.AANGEVRAAGD, "viewing.not_open",
                "Deze aanvraag is al afgehandeld.");
        this.status = Status.BEVESTIGD;
    }

    public void reject() {
        BusinessRuleViolation.requireState(status == Status.AANGEVRAAGD, "viewing.not_open",
                "Deze aanvraag is al afgehandeld.");
        this.status = Status.AFGEWEZEN;
    }

    public void cancel() {
        BusinessRuleViolation.requireState(status == Status.AANGEVRAAGD || status == Status.BEVESTIGD,
                "viewing.not_cancellable", "Deze aanvraag kan niet meer geannuleerd worden.");
        this.status = Status.GEANNULEERD;
    }

    public boolean isOpen() {
        return status == Status.AANGEVRAAGD;
    }

    public UUID id() {
        return id;
    }

    public UUID listingId() {
        return listingId;
    }

    public String requesterName() {
        return requesterName;
    }

    public String email() {
        return email;
    }

    public String phone() {
        return phone;
    }

    public LocalDate preferredDate() {
        return preferredDate;
    }

    public String preferredSlot() {
        return preferredSlot;
    }

    public String message() {
        return message;
    }

    public Status status() {
        return status;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }
}
