package nl.demaker.demaker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.demaker.demaker.dto.request.DeficiencyRequest;
import nl.demaker.demaker.dto.response.DeficiencyResponse;
import nl.demaker.demaker.service.DeficiencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deficiencies")
@RequiredArgsConstructor
public class DeficiencyController {

    private final DeficiencyService deficiencyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MONTEUR')")
    public ResponseEntity<List<DeficiencyResponse>> getAllDeficiencies() {
        List<DeficiencyResponse> deficiencies = deficiencyService.getAllDeficiencies();
        return ResponseEntity.ok(deficiencies);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MONTEUR')")
    public ResponseEntity<DeficiencyResponse> getDeficiencyById(@PathVariable Long id) {
        DeficiencyResponse deficiency = deficiencyService.getDeficiencyById(id);
        return ResponseEntity.ok(deficiency);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MONTEUR')")
    public ResponseEntity<DeficiencyResponse> createDeficiency(@Valid @RequestBody DeficiencyRequest request) {
        DeficiencyResponse deficiency = deficiencyService.createDeficiency(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(deficiency);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeficiencyResponse> updateDeficiency(
            @PathVariable Long id,
            @Valid @RequestBody DeficiencyRequest request) {
        DeficiencyResponse deficiency = deficiencyService.updateDeficiency(id, request);
        return ResponseEntity.ok(deficiency);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDeficiency(@PathVariable Long id) {
        deficiencyService.deleteDeficiency(id);
        return ResponseEntity.noContent().build();
    }
}
