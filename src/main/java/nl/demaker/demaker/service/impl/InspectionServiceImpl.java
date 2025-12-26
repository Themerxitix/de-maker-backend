package nl.demaker.demaker.service.impl;

import lombok.RequiredArgsConstructor;
import nl.demaker.demaker.dto.request.InspectionRequest;
import nl.demaker.demaker.dto.response.InspectionResponse;
import nl.demaker.demaker.exception.ResourceNotFoundException;
import nl.demaker.demaker.model.Car;
import nl.demaker.demaker.model.Inspection;
import nl.demaker.demaker.repository.CarRepository;
import nl.demaker.demaker.repository.InspectionRepository;
import nl.demaker.demaker.service.InspectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {

    private final InspectionRepository inspectionRepository;
    private final CarRepository carRepository;

    @Override
    public InspectionResponse createInspection(InspectionRequest request) {
        // Check if car exists
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", request.getCarId()));

        // Create inspection
        Inspection inspection = new Inspection();
        inspection.setCar(car);
        inspection.setPlannedDate(request.getPlannedDate());
        inspection.setStatus(Inspection.InspectionStatus.PLANNED);

        // Save
        Inspection savedInspection = inspectionRepository.save(inspection);
        return convertToResponse(savedInspection);
    }

    @Override
    public InspectionResponse getInspectionById(Long id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection", "id", id));
        return convertToResponse(inspection);
    }

    @Override
    public List<InspectionResponse> getAllInspections() {
        return inspectionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InspectionResponse updateInspection(Long id, InspectionRequest request) {
        // Find existing inspection
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection", "id", id));

        // Check if car exists
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", request.getCarId()));

        // Update fields
        inspection.setCar(car);
        inspection.setPlannedDate(request.getPlannedDate());

        // Save
        Inspection updatedInspection = inspectionRepository.save(inspection);
        return convertToResponse(updatedInspection);
    }

    @Override
    public void deleteInspection(Long id) {
        if (!inspectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inspection", "id", id);
        }
        inspectionRepository.deleteById(id);
    }

    private InspectionResponse convertToResponse(Inspection inspection) {
        InspectionResponse response = new InspectionResponse();
        response.setId(inspection.getId());
        response.setCarId(inspection.getCar().getId());
        response.setCarLicensePlate(inspection.getCar().getLicensePlate());
        response.setPlannedDate(inspection.getPlannedDate());
        response.setStatus(inspection.getStatus().name());
        return response;
    }
}
