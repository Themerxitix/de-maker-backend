package nl.demaker.demaker.service.impl;

import lombok.RequiredArgsConstructor;
import nl.demaker.demaker.dto.request.DeficiencyRequest;
import nl.demaker.demaker.dto.response.DeficiencyResponse;
import nl.demaker.demaker.exception.ResourceNotFoundException;
import nl.demaker.demaker.model.Deficiency;
import nl.demaker.demaker.model.Inspection;
import nl.demaker.demaker.repository.DeficiencyRepository;
import nl.demaker.demaker.repository.InspectionRepository;
import nl.demaker.demaker.service.DeficiencyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeficiencyServiceImpl implements DeficiencyService {

    private final DeficiencyRepository deficiencyRepository;
    private final InspectionRepository inspectionRepository;

    @Override
    public DeficiencyResponse createDeficiency(DeficiencyRequest request) {
        // Check if inspection exists
        Inspection inspection = inspectionRepository.findById(request.getInspectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Inspection", "id", request.getInspectionId()));

        // Create deficiency
        Deficiency deficiency = new Deficiency();
        deficiency.setInspection(inspection);
        deficiency.setDescription(request.getDescription());
        deficiency.setEstimatedCost(request.getEstimatedCost());
        deficiency.setSafetyRisk(request.getSafetyRisk());

        // Save
        Deficiency savedDeficiency = deficiencyRepository.save(deficiency);
        return convertToResponse(savedDeficiency);
    }

    @Override
    public DeficiencyResponse getDeficiencyById(Long id) {
        Deficiency deficiency = deficiencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deficiency", "id", id));
        return convertToResponse(deficiency);
    }

    @Override
    public List<DeficiencyResponse> getAllDeficiencies() {
        return deficiencyRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeficiencyResponse updateDeficiency(Long id, DeficiencyRequest request) {
        // Find existing deficiency
        Deficiency deficiency = deficiencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deficiency", "id", id));

        // Check if inspection exists
        Inspection inspection = inspectionRepository.findById(request.getInspectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Inspection", "id", request.getInspectionId()));

        // Update fields
        deficiency.setInspection(inspection);
        deficiency.setDescription(request.getDescription());
        deficiency.setEstimatedCost(request.getEstimatedCost());
        deficiency.setSafetyRisk(request.getSafetyRisk());

        // Save
        Deficiency updatedDeficiency = deficiencyRepository.save(deficiency);
        return convertToResponse(updatedDeficiency);
    }

    @Override
    public void deleteDeficiency(Long id) {
        if (!deficiencyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deficiency", "id", id);
        }
        deficiencyRepository.deleteById(id);
    }

    private DeficiencyResponse convertToResponse(Deficiency deficiency) {
        DeficiencyResponse response = new DeficiencyResponse();
        response.setId(deficiency.getId());
        response.setInspectionId(deficiency.getInspection().getId());
        response.setDescription(deficiency.getDescription());
        response.setEstimatedCost(deficiency.getEstimatedCost());
        response.setSafetyRisk(deficiency.getSafetyRisk());
        return response;
    }
}
