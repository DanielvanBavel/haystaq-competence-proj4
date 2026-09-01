package nl.haystaq.huisjacht.makelaars.application;

import nl.haystaq.huisjacht.aanbod.domain.AgencyDirectory;
import nl.haystaq.huisjacht.makelaars.infrastructure.AgencySpringDataRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class AgencyDirectoryAdapter implements AgencyDirectory {

    private final AgencySpringDataRepository agencies;

    public AgencyDirectoryAdapter(AgencySpringDataRepository agencies) {
        this.agencies = agencies;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgencySnapshot> find(UUID agencyId) {
        if (agencyId == null) {
            return Optional.empty();
        }
        return agencies.findById(agencyId).map(agency -> new AgencySnapshot(
                agency.id(), agency.name(), agency.city(), agency.phone(), agency.email(), agency.brandHue()));
    }
}
