package nl.demaker.demaker.repository;

import nl.demaker.demaker.model.Deficiency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeficiencyRepository extends JpaRepository<Deficiency, Long> {

    List<Deficiency> findByInspectionId(Long inspectionId);
}
