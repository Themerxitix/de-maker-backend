package nl.demaker.demaker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeficiencyResponse {

    private Long id;
    private Long inspectionId;
    private String description;
    private Double estimatedCost;
    private Boolean safetyRisk;
}
