package nl.demaker.demaker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.demaker.demaker.dto.request.InspectionRequest;
import nl.demaker.demaker.dto.response.InspectionResponse;
import nl.demaker.demaker.service.InspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MONTEUR')")
    public ResponseEntity<List<InspectionResponse>> getAllInspections() {
        List<InspectionResponse> inspections = inspectionService.getAllInspections();
        return ResponseEntity.ok(inspections);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MONTEUR')")
    public ResponseEntity<InspectionResponse> getInspectionById(@PathVariable Long id) {
        InspectionResponse inspection = inspectionService.getInspectionById(id);
        return ResponseEntity.ok(inspection);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MONTEUR')")
    public ResponseEntity<InspectionResponse> createInspection(@Valid @RequestBody InspectionRequest request) {
        InspectionResponse inspection = inspectionService.createInspection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inspection);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InspectionResponse> updateInspection(
            @PathVariable Long id,
            @Valid @RequestBody InspectionRequest request) {
        InspectionResponse inspection = inspectionService.updateInspection(id, request);
        return ResponseEntity.ok(inspection);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInspection(@PathVariable Long id) {
        inspectionService.deleteInspection(id);
        return ResponseEntity.noContent().build();
    }
}
