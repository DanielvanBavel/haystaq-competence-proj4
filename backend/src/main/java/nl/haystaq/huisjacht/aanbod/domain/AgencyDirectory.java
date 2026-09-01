package nl.haystaq.huisjacht.aanbod.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Poort naar het context makelaars. Het aanbod hoeft alleen te weten wat er op
 * de woningpagina getoond wordt.
 */
public interface AgencyDirectory {

    record AgencySnapshot(UUID id, String name, String city, String phone, String email, int brandHue) {
    }

    Optional<AgencySnapshot> find(UUID agencyId);
}
