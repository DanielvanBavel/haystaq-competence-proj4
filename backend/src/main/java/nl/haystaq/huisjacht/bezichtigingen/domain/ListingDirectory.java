package nl.haystaq.huisjacht.bezichtigingen.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Poort naar het context aanbod. Bezichtigingen hoeft alleen te weten of de
 * woning bestaat en of er nog bezichtigd mag worden.
 */
public interface ListingDirectory {

    record ListingSnapshot(UUID id, String reference, String address, boolean acceptsViewings, String status) {
    }

    Optional<ListingSnapshot> find(UUID listingId);

    Optional<ListingSnapshot> findByReference(String reference);
}
