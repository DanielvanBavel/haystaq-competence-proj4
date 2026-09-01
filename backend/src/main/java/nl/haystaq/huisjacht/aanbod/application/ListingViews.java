package nl.haystaq.huisjacht.aanbod.application;

import nl.haystaq.huisjacht.aanbod.domain.AgencyDirectory;
import nl.haystaq.huisjacht.aanbod.domain.Listing;
import nl.haystaq.huisjacht.aanbod.domain.ListingPhoto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class ListingViews {

    private ListingViews() {
    }

    public record PhotoView(UUID id, int position, String room, String caption, boolean inTour, String url) {
        public static PhotoView of(ListingPhoto photo) {
            return new PhotoView(photo.id(), photo.position(), photo.room(), photo.caption(), photo.isInTour(),
                    "/api/media/photos/" + photo.id());
        }
    }

    public record AgencyView(UUID id, String name, String city, String phone, String email, int brandHue) {
    }

    public record SummaryView(
            UUID id,
            String reference,
            String street,
            String houseNumber,
            String postalCode,
            String city,
            String district,
            BigDecimal price,
            String status,
            String propertyType,
            int livingAreaM2,
            Integer plotAreaM2,
            int rooms,
            int bedrooms,
            int buildYear,
            String energyLabel,
            boolean hasGarden,
            boolean hasBalcony,
            boolean hasParking,
            long pricePerSquareMetre,
            int photoCount,
            String coverPhotoUrl,
            OffsetDateTime publishedAt) {

        public static SummaryView of(Listing listing, int photoCount, UUID coverPhotoId) {
            return new SummaryView(
                    listing.id(),
                    listing.reference(),
                    listing.address().street(),
                    listing.address().houseNumber(),
                    listing.address().postalCode(),
                    listing.address().city(),
                    listing.address().district(),
                    listing.price().amount(),
                    listing.status().name(),
                    listing.propertyType().name(),
                    listing.livingAreaM2(),
                    listing.plotAreaM2(),
                    listing.rooms(),
                    listing.bedrooms(),
                    listing.buildYear(),
                    listing.energyLabel(),
                    listing.hasGarden(),
                    listing.hasBalcony(),
                    listing.hasParking(),
                    listing.pricePerSquareMetre(),
                    photoCount,
                    coverPhotoId == null ? null : "/api/media/photos/" + coverPhotoId,
                    listing.publishedAt());
        }
    }

    public record DetailView(
            SummaryView summary,
            String description,
            BigDecimal serviceCosts,
            BigDecimal latitude,
            BigDecimal longitude,
            AgencyView agency,
            List<PhotoView> photos,
            List<PhotoView> tour) {

        public static DetailView of(Listing listing, List<ListingPhoto> photos,
                                    AgencyDirectory.AgencySnapshot agency) {
            List<PhotoView> all = photos.stream().map(PhotoView::of).toList();
            return new DetailView(
                    SummaryView.of(listing, all.size(), all.isEmpty() ? null : all.get(0).id()),
                    listing.description(),
                    listing.serviceCosts() == null ? null : listing.serviceCosts().amount(),
                    listing.latitude(),
                    listing.longitude(),
                    agency == null ? null : new AgencyView(agency.id(), agency.name(), agency.city(),
                            agency.phone(), agency.email(), agency.brandHue()),
                    all,
                    all.stream().filter(PhotoView::inTour).toList());
        }
    }

    public record SearchResult(
            List<SummaryView> results,
            int totalResults,
            int page,
            int pageSize,
            int totalPages) {
    }
}
