package DuongVanBao.event.controller;

import DuongVanBao.event.dto.response.ReportResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-khach-hang")
    public ResponseEntity<?> getTopKhachHang(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) Integer limit) {

        if (tuNgay == null) {
            tuNgay = LocalDate.now().minusYears(1); // Default to 1 year ago
        }
        if (denNgay == null) {
            denNgay = LocalDate.now(); // Default to today
        }

        List<ReportResponse.KhachHangMuaNhieu> topKhachHang = reportService.getTopKhachHangByDateRange(tuNgay, denNgay, limit);
        return ResponseEntity.ok(SuccessResponse.withData(topKhachHang));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-khach-hang/{maSuKien}")
    public ResponseEntity<?> getTopKhachHangBySuKien(
            @PathVariable String maSuKien,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) Integer limit) {

        if (tuNgay == null) {
            tuNgay = LocalDate.now().minusYears(1);
        }
        if (denNgay == null) {
            denNgay = LocalDate.now();
        }

        List<ReportResponse.KhachHangMuaNhieu> topKhachHang = reportService.getTopKhachHangBySuKien(maSuKien, tuNgay, denNgay, limit);
        return ResponseEntity.ok(SuccessResponse.withData(topKhachHang));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/su-kien/{maSuKien}")
    public ResponseEntity<?> getDoanhThuAndDetailsSuKien(@PathVariable String maSuKien) {
        ReportResponse.DoanhThuSuKien response = reportService.getDoanhThuAndDetailsSuKien(maSuKien);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(SuccessResponse.withData(response));
    }
}
