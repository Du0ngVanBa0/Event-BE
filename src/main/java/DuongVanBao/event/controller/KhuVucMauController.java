package DuongVanBao.event.controller;

import DuongVanBao.event.dto.response.KhuVucMauResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.model.entity.KhuVucMau;
import DuongVanBao.event.service.KhuVucMauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/khu-vuc-mau")
public class KhuVucMauController {
    private final KhuVucMauService khuVucMauService;

    public KhuVucMauController(KhuVucMauService khuVucMauService) {
        this.khuVucMauService = khuVucMauService;
    }

    @GetMapping("")
    public ResponseEntity<?> getActiveTemplates() {
        List<KhuVucMau> templates = khuVucMauService.findByHoatDongTrueOrderByThuTuHienThi();
        List<KhuVucMauResponse> response = templates.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(SuccessResponse.withData(response));
    }

    private KhuVucMauResponse convertToResponse(KhuVucMau template) {
        KhuVucMauResponse response = new KhuVucMauResponse();
        response.setMaKhuVucMau(template.getMaKhuVucMau());
        response.setTenKhuVuc(template.getTenKhuVuc());
        response.setMoTa(template.getMoTa());
        response.setMauSac(template.getMauSac());
        response.setHinhDang(template.getHinhDang());
        response.setThuTuHienThi(template.getThuTuHienThi());
        response.setHoatDong(template.isHoatDong());
        response.setToaDoXMacDinh(template.getToaDoXMacDinh());
        response.setToaDoYMacDinh(template.getToaDoYMacDinh());
        response.setChieuRongMacDinh(template.getChieuRongMacDinh());
        response.setChieuCaoMacDinh(template.getChieuCaoMacDinh());
        return response;
    }
}
