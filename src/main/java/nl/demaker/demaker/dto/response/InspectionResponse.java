package nl.demaker.demaker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionResponse {

    private Long id;
    private Long carId;
    private String carLicensePlate;
    private LocalDate plannedDate;
    private String status;
}
