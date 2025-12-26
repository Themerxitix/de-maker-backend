package nl.demaker.demaker.service.impl;

import lombok.RequiredArgsConstructor;
import nl.demaker.demaker.dto.response.CarPapersResponse;
import nl.demaker.demaker.exception.BadRequestException;
import nl.demaker.demaker.exception.ResourceNotFoundException;
import nl.demaker.demaker.model.Car;
import nl.demaker.demaker.model.CarPapers;
import nl.demaker.demaker.repository.CarPapersRepository;
import nl.demaker.demaker.repository.CarRepository;
import nl.demaker.demaker.service.CarPapersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CarPapersServiceImpl implements CarPapersService {

    private final CarPapersRepository carPapersRepository;
    private final CarRepository carRepository;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    @Override
    @Transactional
    public CarPapersResponse uploadPapers(Long carId, MultipartFile file) {
        // Check if car exists
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));

        // Validate file
        validateFile(file);

        try {
            // Convert to byte array
            byte[] fileData = file.getBytes();

            // Check if papers already exist for this car
            CarPapers carPapers = carPapersRepository.findByCarId(carId)
                    .orElse(new CarPapers());

            // Update or create
            carPapers.setFileName(file.getOriginalFilename());
            carPapers.setUploadDate(LocalDate.now());
            carPapers.setFileData(fileData);
            carPapers.setCar(car);

            // Save
            CarPapers savedPapers = carPapersRepository.save(carPapers);

            return convertToResponse(savedPapers);

        } catch (IOException e) {
            throw new BadRequestException("Failed to read file: " + e.getMessage());
        }
    }

    @Override
    public CarPapers getPapers(Long carId) {
        // Find car
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));

        // Get papers
        CarPapers papers = car.getCarPapers();
        if (papers == null) {
            throw new ResourceNotFoundException("Car papers not found for car with id: " + carId);
        }

        return papers;
    }

    @Override
    @Transactional
    public void deletePapers(Long carId) {
        // Check if car exists
        if (!carRepository.existsById(carId)) {
            throw new ResourceNotFoundException("Car", "id", carId);
        }

        // Check if papers exist
        CarPapers papers = carPapersRepository.findByCarId(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car papers not found for car with id: " + carId));

        carPapersRepository.delete(papers);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals(PDF_CONTENT_TYPE)) {
            throw new BadRequestException("Only PDF files are allowed");
        }

        // Validate file size (max 10MB)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size must not exceed 10MB");
        }
    }

    private CarPapersResponse convertToResponse(CarPapers carPapers) {
        CarPapersResponse response = new CarPapersResponse();
        response.setId(carPapers.getId());
        response.setFileName(carPapers.getFileName());
        response.setUploadDate(carPapers.getUploadDate());
        response.setCarId(carPapers.getCar().getId());
        return response;
    }
}
