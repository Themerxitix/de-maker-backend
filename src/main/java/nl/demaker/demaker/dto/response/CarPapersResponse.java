package nl.demaker.demaker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarPapersResponse {

    private Long id;
    private String fileName;
    private LocalDate uploadDate;
    private Long carId;
}
