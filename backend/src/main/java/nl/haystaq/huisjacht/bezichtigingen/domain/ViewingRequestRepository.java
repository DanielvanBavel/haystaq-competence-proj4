package nl.haystaq.huisjacht.bezichtigingen.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ViewingRequestRepository {

    ViewingRequest save(ViewingRequest request);

    Optional<ViewingRequest> findById(UUID id);

    List<ViewingRequest> findByListing(UUID listingId);

    List<ViewingRequest> findAll();

    long countOpenFor(UUID listingId, String email);

    List<String> takenSlots(UUID listingId, LocalDate date);
}
