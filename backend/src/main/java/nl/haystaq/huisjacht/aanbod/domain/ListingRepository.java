package nl.haystaq.huisjacht.aanbod.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository {

    Optional<Listing> findById(UUID id);

    Optional<Listing> findByReference(String reference);

    List<Listing> search(String city, BigDecimal minPrice, BigDecimal maxPrice, Integer minRooms,
                         Integer minLivingArea, String propertyType, Boolean garden, String status, String term);

    List<String> cities();

    Optional<ListingPhoto> findPhoto(UUID photoId);

    Listing save(Listing listing);
}
