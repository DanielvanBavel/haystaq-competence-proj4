package nl.haystaq.huisjacht.aanbod.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import nl.haystaq.huisjacht.shared.domain.BusinessRuleViolation;

import java.util.Locale;
import java.util.regex.Pattern;

/** Waarde-object: het adres van een woning. */
@Embeddable
public class Address {

    private static final Pattern POSTAL_CODE = Pattern.compile("^[1-9][0-9]{3} ?[A-Z]{2}$");

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district")
    private String district;

    protected Address() {
        // voor JPA
    }

    public Address(String street, String houseNumber, String postalCode, String city, String district) {
        BusinessRuleViolation.require(street != null && !street.isBlank(), "address.street", "Straat ontbreekt.");
        BusinessRuleViolation.require(houseNumber != null && !houseNumber.isBlank(), "address.house_number",
                "Huisnummer ontbreekt.");
        String normalised = postalCode == null ? "" : postalCode.trim().toUpperCase(Locale.ROOT);
        BusinessRuleViolation.require(POSTAL_CODE.matcher(normalised).matches(), "address.postal_code",
                "Postcode moet de vorm 1234 AB hebben.");
        BusinessRuleViolation.require(city != null && !city.isBlank(), "address.city", "Plaats ontbreekt.");

        this.street = street.trim();
        this.houseNumber = houseNumber.trim();
        this.postalCode = normalised.length() == 6
                ? normalised.substring(0, 4) + " " + normalised.substring(4)
                : normalised;
        this.city = city.trim();
        this.district = district == null || district.isBlank() ? null : district.trim();
    }

    public String full() {
        return "%s %s, %s %s".formatted(street, houseNumber, postalCode, city);
    }

    public String street() {
        return street;
    }

    public String houseNumber() {
        return houseNumber;
    }

    public String postalCode() {
        return postalCode;
    }

    public String city() {
        return city;
    }

    public String district() {
        return district;
    }
}
