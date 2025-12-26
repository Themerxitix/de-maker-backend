package nl.demaker.demaker.repository;

import nl.demaker.demaker.model.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    List<Inspection> findByCarId(Long carId);
}
