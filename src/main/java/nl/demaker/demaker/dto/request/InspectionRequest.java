package nl.demaker.demaker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionRequest {

    @NotNull(message = "Car ID is required")
    private Long carId;

    @NotNull(message = "Planned date is required")
    private LocalDate plannedDate;
}
