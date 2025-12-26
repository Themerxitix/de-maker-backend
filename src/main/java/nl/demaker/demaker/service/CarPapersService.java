package nl.demaker.demaker.service;

import nl.demaker.demaker.dto.response.CarPapersResponse;
import nl.demaker.demaker.model.CarPapers;
import org.springframework.web.multipart.MultipartFile;

public interface CarPapersService {

    CarPapersResponse uploadPapers(Long carId, MultipartFile file);

    CarPapers getPapers(Long carId);

    void deletePapers(Long carId);
}
