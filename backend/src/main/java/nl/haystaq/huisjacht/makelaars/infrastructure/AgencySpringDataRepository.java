package nl.haystaq.huisjacht.makelaars.infrastructure;

import nl.haystaq.huisjacht.makelaars.domain.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgencySpringDataRepository extends JpaRepository<Agency, UUID> {

    List<Agency> findAllByOrderByNameAsc();
}
