package nl.demaker.demaker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "car_papers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarPapers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private LocalDate uploadDate;

    @Lob
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] fileData;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;
}
