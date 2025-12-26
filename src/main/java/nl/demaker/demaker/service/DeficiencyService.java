package nl.demaker.demaker.service;

import nl.demaker.demaker.dto.request.DeficiencyRequest;
import nl.demaker.demaker.dto.response.DeficiencyResponse;

import java.util.List;

public interface DeficiencyService {

    DeficiencyResponse createDeficiency(DeficiencyRequest request);

    DeficiencyResponse getDeficiencyById(Long id);

    List<DeficiencyResponse> getAllDeficiencies();

    DeficiencyResponse updateDeficiency(Long id, DeficiencyRequest request);

    void deleteDeficiency(Long id);
}
