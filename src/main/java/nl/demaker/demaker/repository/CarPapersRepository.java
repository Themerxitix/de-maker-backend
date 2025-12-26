package nl.demaker.demaker.repository;

import nl.demaker.demaker.model.CarPapers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarPapersRepository extends JpaRepository<CarPapers, Long> {

    Optional<CarPapers> findByCarId(Long carId);

    void deleteByCarId(Long carId);
}
