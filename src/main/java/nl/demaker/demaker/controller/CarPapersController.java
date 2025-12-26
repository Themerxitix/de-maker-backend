package nl.demaker.demaker.controller;

import lombok.RequiredArgsConstructor;
import nl.demaker.demaker.dto.response.CarPapersResponse;
import nl.demaker.demaker.model.CarPapers;
import nl.demaker.demaker.service.CarPapersService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cars/{carId}/papers")
@RequiredArgsConstructor
public class CarPapersController {

    private final CarPapersService carPapersService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarPapersResponse> uploadPapers(
            @PathVariable Long carId,
            @RequestParam("file") MultipartFile file) {
        CarPapersResponse response = carPapersService.uploadPapers(carId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPapers(@PathVariable Long carId) {
        CarPapers papers = carPapersService.getPapers(carId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", papers.getFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(papers.getFileData());
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePapers(@PathVariable Long carId) {
        carPapersService.deletePapers(carId);
        return ResponseEntity.noContent().build();
    }
}
