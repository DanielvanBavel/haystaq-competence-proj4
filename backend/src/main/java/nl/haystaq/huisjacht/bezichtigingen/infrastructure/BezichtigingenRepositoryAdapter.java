package nl.haystaq.huisjacht.bezichtigingen.infrastructure;

import nl.haystaq.huisjacht.bezichtigingen.domain.ViewingRequest;
import nl.haystaq.huisjacht.bezichtigingen.domain.ViewingRequestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class BezichtigingenRepositoryAdapter {

    @Bean
    ViewingRequestRepository viewingRequestRepository(ViewingSpringDataRepository delegate) {
        return new ViewingRequestRepository() {
            @Override
            public ViewingRequest save(ViewingRequest request) {
                return delegate.save(request);
            }

            @Override
            public Optional<ViewingRequest> findById(UUID id) {
                return delegate.findById(id);
            }

            @Override
            public List<ViewingRequest> findByListing(UUID listingId) {
                return delegate.findByListingIdOrderByPreferredDateAscPreferredSlotAsc(listingId);
            }

            @Override
            public List<ViewingRequest> findAll() {
                return delegate.findAllByOrderByCreatedAtDesc();
            }

            @Override
            public long countOpenFor(UUID listingId, String email) {
                return delegate.countOpenFor(listingId, email);
            }

            @Override
            public List<String> takenSlots(UUID listingId, LocalDate date) {
                return delegate.takenSlots(listingId, date);
            }
        };
    }
}
