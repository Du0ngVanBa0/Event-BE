package DuongVanBao.event.controller;

import DuongVanBao.event.dto.message.DanhGiaMessage;
import DuongVanBao.event.dto.request.DanhGiaRequest;
import DuongVanBao.event.dto.response.DanhGiaResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.model.entity.DanhGia;
import DuongVanBao.event.service.DanhGiaService;
import DuongVanBao.event.service.SuKienService;
import DuongVanBao.event.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{maSuKien}/reviews")
@RequiredArgsConstructor
public class DanhGiaController {
    private final DanhGiaService danhGiaService;
    private final SuKienService suKienService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<?> getReviews(@PathVariable String maSuKien, Pageable pageable) {
        var suKien = suKienService.findById(maSuKien)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        Page<DanhGiaResponse> responses = danhGiaService.findBySuKien(suKien, pageable)
            .map(this::toDanhGiaResponse);

        return ResponseEntity.ok(SuccessResponse.withData(responses));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> create(
        @PathVariable String maSuKien,
        @RequestBody @Valid DanhGiaRequest request
    ) {
        var suKien = suKienService.findById(maSuKien)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
            
        var nguoiDung = SecurityUtils.getCurrentUser();
        
        if (danhGiaService.existsByNguoiDungAndSuKien(nguoiDung.getMaNguoiDung(), maSuKien)) {
            throw new RuntimeException("Bạn đã đánh giá sự kiện này");
        }

        DanhGia danhGia = new DanhGia();
        danhGia.setSuKien(suKien);
        danhGia.setNguoiDung(nguoiDung);
        danhGia.setNoiDung(request.getNoiDung());
        danhGia.setDiemDanhGia(request.getDiemDanhGia());
        danhGia = danhGiaService.save(danhGia);

        DanhGiaResponse response = toDanhGiaResponse(danhGia);
        messagingTemplate.convertAndSend(
                "/topic/events/" + maSuKien + "/reviews",
                new DanhGiaMessage("NEW_REVIEW", response)
        );

        return ResponseEntity.ok(SuccessResponse.withData(response));
    }

    private DanhGiaResponse toDanhGiaResponse(DanhGia danhGia) {
        DanhGiaResponse response = new DanhGiaResponse();
        response.setMaDanhGia(danhGia.getMaDanhGia());
        response.setNoiDung(danhGia.getNoiDung());
        response.setDiemDanhGia(danhGia.getDiemDanhGia());
        response.setNgayTao(danhGia.getNgayTao());
        response.setHoTenNguoiDung(danhGia.getNguoiDung().getTenHienThi());
        response.setAnhDaiDienNguoiDung(danhGia.getNguoiDung().getAnhDaiDien());
        return response;
    }
}