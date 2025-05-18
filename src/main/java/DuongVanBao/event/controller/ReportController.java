package DuongVanBao.event.controller;

import DuongVanBao.event.dto.response.ReportResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.service.DatVeService;
import DuongVanBao.event.service.NguoiDungService;
import DuongVanBao.event.service.SuKienService;
import DuongVanBao.event.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/thong-ke")
@AllArgsConstructor
public class ReportController {
    private final SuKienService suKienService;
    private final NguoiDungService nguoiDungService;
    private final DatVeService datVeService;
    private final ReportService reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> getThongKe(
            @RequestParam(required = false) Integer thang,
            @RequestParam(required = false) Integer nam) {

        ReportResponse response = reportService.getThongKe(thang, nam);
        return ResponseEntity.ok(SuccessResponse.withData(response));
    }
}
