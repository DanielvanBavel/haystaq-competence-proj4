package nl.haystaq.huisjacht.aanbod.application;

import nl.haystaq.huisjacht.aanbod.domain.Listing;
import nl.haystaq.huisjacht.aanbod.domain.ListingRepository;
import nl.haystaq.huisjacht.bezichtigingen.domain.ListingDirectory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class ListingDirectoryAdapter implements ListingDirectory {

    private final ListingRepository listings;

    public ListingDirectoryAdapter(ListingRepository listings) {
        this.listings = listings;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ListingSnapshot> find(UUID listingId) {
        return listingId == null ? Optional.empty() : listings.findById(listingId).map(ListingDirectoryAdapter::snapshot);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ListingSnapshot> findByReference(String reference) {
        return reference == null
                ? Optional.empty()
                : listings.findByReference(reference).map(ListingDirectoryAdapter::snapshot);
    }

    private static ListingSnapshot snapshot(Listing listing) {
        return new ListingSnapshot(listing.id(), listing.reference(), listing.address().full(),
                listing.status().acceptsViewings(), listing.status().name());
    }
}
