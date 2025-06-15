package DuongVanBao.event.controller;

import DuongVanBao.event.dto.response.ReportResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/thong-ke")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> getThongKeAllTime() {
        ReportResponse response = reportService.getThongKeAllTime();
        return ResponseEntity.ok(SuccessResponse.withData(response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/range")
    public ResponseEntity<?> getThongKeByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        ReportResponse response = reportService.getThongKeByDateRange(tuNgay, denNgay);
        return ResponseEntity.ok(SuccessResponse.withData(response));
    }
}
