package nl.haystaq.huisjacht.makelaars.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** Aggregate root van het context makelaars. */
@Entity
@Table(name = "agency")
public class Agency {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "brand_hue", nullable = false)
    private int brandHue;

    protected Agency() {
        // voor JPA
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String city() {
        return city;
    }

    public String phone() {
        return phone;
    }

    public String email() {
        return email;
    }

    public int brandHue() {
        return brandHue;
    }
}
