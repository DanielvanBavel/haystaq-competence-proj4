package nl.haystaq.huisjacht.aanbod.infrastructure;

import nl.haystaq.huisjacht.aanbod.domain.Listing;
import nl.haystaq.huisjacht.aanbod.domain.ListingPhoto;
import nl.haystaq.huisjacht.aanbod.domain.ListingRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class AanbodRepositoryAdapters {

    @Bean
    ListingRepository listingRepository(ListingSpringDataRepository delegate) {
        return new ListingRepository() {
            @Override
            public Optional<Listing> findById(UUID id) {
                return delegate.findById(id);
            }

            @Override
            public Optional<Listing> findByReference(String reference) {
                return delegate.findByReference(reference);
            }

            @Override
            public List<Listing> search(String city, BigDecimal minPrice, BigDecimal maxPrice, Integer minRooms,
                                        Integer minLivingArea, String propertyType, Boolean garden, String status,
                                        String term) {
                return delegate.search(city, minPrice, maxPrice, minRooms, minLivingArea, propertyType, garden,
                        status, term);
            }

            @Override
            public List<String> cities() {
                return delegate.cities();
            }

            @Override
            public Optional<ListingPhoto> findPhoto(UUID photoId) {
                return delegate.findPhoto(photoId);
            }

            @Override
            public Listing save(Listing listing) {
                return delegate.save(listing);
            }
        };
    }
}
