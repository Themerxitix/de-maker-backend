package nl.demaker.demaker.service;

import nl.demaker.demaker.dto.request.InspectionRequest;
import nl.demaker.demaker.dto.response.InspectionResponse;

import java.util.List;

public interface InspectionService {

    InspectionResponse createInspection(InspectionRequest request);

    InspectionResponse getInspectionById(Long id);

    List<InspectionResponse> getAllInspections();

    InspectionResponse updateInspection(Long id, InspectionRequest request);

    void deleteInspection(Long id);
}
