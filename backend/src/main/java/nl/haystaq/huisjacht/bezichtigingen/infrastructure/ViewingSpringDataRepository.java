package nl.haystaq.huisjacht.bezichtigingen.infrastructure;

import nl.haystaq.huisjacht.bezichtigingen.domain.ViewingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ViewingSpringDataRepository extends JpaRepository<ViewingRequest, UUID> {

    List<ViewingRequest> findByListingIdOrderByPreferredDateAscPreferredSlotAsc(UUID listingId);

    List<ViewingRequest> findAllByOrderByCreatedAtDesc();

    @Query("""
            select count(v) from ViewingRequest v
            where v.listingId = :listingId and lower(v.email) = lower(:email) and v.status = 'AANGEVRAAGD'
            """)
    long countOpenFor(UUID listingId, String email);

    @Query("""
            select v.preferredSlot from ViewingRequest v
            where v.listingId = :listingId and v.preferredDate = :date
              and v.status in ('AANGEVRAAGD', 'BEVESTIGD')
            """)
    List<String> takenSlots(UUID listingId, LocalDate date);
}
