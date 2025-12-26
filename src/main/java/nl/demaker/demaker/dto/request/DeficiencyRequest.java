package nl.demaker.demaker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeficiencyRequest {

    @NotNull(message = "Inspection ID is required")
    private Long inspectionId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Estimated cost is required")
    private Double estimatedCost;

    @NotNull(message = "Safety risk indication is required")
    private Boolean safetyRisk;
}
