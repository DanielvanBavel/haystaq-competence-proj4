package nl.haystaq.huisjacht.aanbod.infrastructure;

import nl.haystaq.huisjacht.aanbod.domain.Listing;
import nl.haystaq.huisjacht.aanbod.domain.ListingPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingSpringDataRepository extends JpaRepository<Listing, UUID> {

    Optional<Listing> findByReference(String reference);

    /**
     * Zoeken met optionele filters. Native query met expliciete casts: de prijs is
     * een waarde-object en Postgres wil weten wat het type van een null-parameter is.
     */
    @Query(value = """
            select l.* from listing l
            where (cast(:city as text) is null or lower(l.city) = lower(cast(:city as text)))
              and (cast(:minPrice as numeric) is null or l.price >= cast(:minPrice as numeric))
              and (cast(:maxPrice as numeric) is null or l.price <= cast(:maxPrice as numeric))
              and (cast(:minRooms as integer) is null or l.rooms >= cast(:minRooms as integer))
              and (cast(:minLivingArea as integer) is null or l.living_area_m2 >= cast(:minLivingArea as integer))
              and (cast(:propertyType as text) is null or l.property_type = cast(:propertyType as text))
              and (cast(:garden as boolean) is null or l.has_garden = cast(:garden as boolean))
              and (cast(:status as text) is null or l.status = cast(:status as text))
              and (cast(:term as text) is null
                   or lower(l.street) like lower('%' || cast(:term as text) || '%')
                   or lower(l.city) like lower('%' || cast(:term as text) || '%')
                   or lower(coalesce(l.district, '')) like lower('%' || cast(:term as text) || '%'))
            order by l.published_at desc
            """, nativeQuery = true)
    List<Listing> search(String city, BigDecimal minPrice, BigDecimal maxPrice, Integer minRooms,
                         Integer minLivingArea, String propertyType, Boolean garden, String status, String term);

    @Query(value = "select distinct city from listing order by 1", nativeQuery = true)
    List<String> cities();

    @Query("select p from ListingPhoto p where p.id = :photoId")
    Optional<ListingPhoto> findPhoto(UUID photoId);

    @Query("select p from ListingPhoto p where p.listing.id = :listingId order by p.position")
    List<ListingPhoto> findPhotosOf(UUID listingId);
}
