package nl.haystaq.huisjacht.aanbod.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import nl.haystaq.huisjacht.shared.domain.BusinessRuleViolation;
import nl.haystaq.huisjacht.shared.domain.Money;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Aggregate root van het context aanbod: een woning die te koop staat. */
@Entity
@Table(name = "listing")
public class Listing {

    public enum Status {
        BESCHIKBAAR,
        ONDER_BOD,
        VERKOCHT,
        INGETROKKEN;

        public boolean acceptsViewings() {
            return this == BESCHIKBAAR || this == ONDER_BOD;
        }
    }

    public enum PropertyType {
        APPARTEMENT,
        TUSSENWONING,
        HOEKWONING,
        TWEE_ONDER_EEN_KAP,
        VRIJSTAAND,
        BENEDENWONING;

        public static PropertyType parse(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }
            try {
                return valueOf(raw.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw BusinessRuleViolation.invalid("property_type.unknown",
                        "Onbekend woningtype: " + raw);
            }
        }
    }

    @Id
    private UUID id;

    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Embedded
    private Address address;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

    @Column(name = "price", nullable = false)
    private Money price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;

    @Column(name = "living_area_m2", nullable = false)
    private int livingAreaM2;

    @Column(name = "plot_area_m2")
    private Integer plotAreaM2;

    @Column(name = "rooms", nullable = false)
    private int rooms;

    @Column(name = "bedrooms", nullable = false)
    private int bedrooms;

    @Column(name = "build_year", nullable = false)
    private int buildYear;

    @Column(name = "energy_label", nullable = false)
    private String energyLabel;

    @Column(name = "has_garden", nullable = false)
    private boolean hasGarden;

    @Column(name = "has_balcony", nullable = false)
    private boolean hasBalcony;

    @Column(name = "has_parking", nullable = false)
    private boolean hasParking;

    @Column(name = "service_costs")
    private Money serviceCosts;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "agency_id", nullable = false)
    private UUID agencyId;

    @Column(name = "published_at", nullable = false)
    private OffsetDateTime publishedAt;

    @OneToMany(mappedBy = "listing", fetch = FetchType.LAZY)
    @OrderBy("position")
    private List<ListingPhoto> photos = new ArrayList<>();

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected Listing() {
        // voor JPA
    }

    /** Prijs per vierkante meter, afgerond op hele euro's. */
    public long pricePerSquareMetre() {
        return price.amount().longValue() / Math.max(1, livingAreaM2);
    }

    public void assertAcceptsViewings() {
        BusinessRuleViolation.requireState(status.acceptsViewings(), "listing.not_viewable",
                "Voor deze woning kun je geen bezichtiging meer aanvragen.");
    }

    public void changeStatus(Status next) {
        BusinessRuleViolation.requireState(status != Status.VERKOCHT || next == Status.VERKOCHT,
                "listing.sold", "Een verkochte woning kan niet terug in de verkoop.");
        this.status = next;
    }

    public UUID id() {
        return id;
    }

    public String reference() {
        return reference;
    }

    public Address address() {
        return address;
    }

    public BigDecimal latitude() {
        return latitude;
    }

    public BigDecimal longitude() {
        return longitude;
    }

    public Money price() {
        return price;
    }

    public Status status() {
        return status;
    }

    public PropertyType propertyType() {
        return propertyType;
    }

    public int livingAreaM2() {
        return livingAreaM2;
    }

    public Integer plotAreaM2() {
        return plotAreaM2;
    }

    public int rooms() {
        return rooms;
    }

    public int bedrooms() {
        return bedrooms;
    }

    public int buildYear() {
        return buildYear;
    }

    public String energyLabel() {
        return energyLabel;
    }

    public boolean hasGarden() {
        return hasGarden;
    }

    public boolean hasBalcony() {
        return hasBalcony;
    }

    public boolean hasParking() {
        return hasParking;
    }

    public Money serviceCosts() {
        return serviceCosts;
    }

    public String description() {
        return description;
    }

    public UUID agencyId() {
        return agencyId;
    }

    public OffsetDateTime publishedAt() {
        return publishedAt;
    }

    public List<ListingPhoto> photos() {
        return List.copyOf(photos);
    }
}
