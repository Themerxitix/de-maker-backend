package nl.demaker.demaker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {

    private Long id;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private Long customerId;
    private String customerName;
}
