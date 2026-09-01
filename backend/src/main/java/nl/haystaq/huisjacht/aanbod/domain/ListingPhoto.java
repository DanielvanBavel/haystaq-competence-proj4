package nl.haystaq.huisjacht.aanbod.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

/** Entiteit binnen het aggregate {@link Listing}. */
@Entity
@Table(name = "listing_photo")
public class ListingPhoto {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "room", nullable = false)
    private String room;

    @Column(name = "caption", nullable = false)
    private String caption;

    /** Kleurtoon waarmee de afbeelding wordt gegenereerd; geen externe media nodig. */
    @Column(name = "hue", nullable = false)
    private int hue;

    @Column(name = "in_tour", nullable = false)
    private boolean inTour;

    protected ListingPhoto() {
        // voor JPA
    }

    public UUID id() {
        return id;
    }

    public int position() {
        return position;
    }

    public String room() {
        return room;
    }

    public String caption() {
        return caption;
    }

    public int hue() {
        return hue;
    }

    public boolean isInTour() {
        return inTour;
    }
}
