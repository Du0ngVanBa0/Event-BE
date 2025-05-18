package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.DanhMucRequest;
import DuongVanBao.event.dto.response.ErrorResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.model.entity.DanhMucSuKien;
import DuongVanBao.event.service.DanhMucSuKienService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/danh-muc")
@RequiredArgsConstructor
public class DanhMucSuKienController implements BaseController<DanhMucRequest, String> {
    private final DanhMucSuKienService service;

    @Override
    public ResponseEntity<?> getAll() {
        List<DanhMucSuKien> list = service.findAll();
        return ResponseEntity.ok(SuccessResponse.withData(list));
    }

    @Override
    public ResponseEntity<?> getPage(Pageable pageable) {
        Page<DanhMucSuKien> page = service.findAll(pageable);
        return ResponseEntity.ok(SuccessResponse.withData(page));
    }

    @Override
    public ResponseEntity<?> getById(String id) {
        Optional<DanhMucSuKien> thisObj = service.findById(id);
        if (thisObj.isPresent()) {
            return ResponseEntity.ok(SuccessResponse.withData(thisObj.get()));
        } else {
            return ResponseEntity.ok(ErrorResponse.withMessage("Không tìm thấy danh mục"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<?> create(DanhMucRequest request) {
        DanhMucSuKien danhMuc = new DanhMucSuKien();
        danhMuc.setTenDanhMuc(request.getTenDanhMuc());
        danhMuc.setMoTa(request.getMoTa());
        danhMuc.setHoatDong(true);

        DanhMucSuKien saved = service.save(danhMuc);
        return ResponseEntity.ok(SuccessResponse.withMessage(saved, "Tạo danh mục thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<?> update(String id, DanhMucRequest request) {
        Optional<DanhMucSuKien> existing = service.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DanhMucSuKien danhMuc = existing.get();
        danhMuc.setTenDanhMuc(request.getTenDanhMuc());
        danhMuc.setMoTa(request.getMoTa());
        danhMuc.setHoatDong(request.isHoatDong());

        DanhMucSuKien updated = service.save(danhMuc);
        return ResponseEntity.ok(SuccessResponse.withMessage(updated, "Cập nhật danh mục thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<?> delete(String id) {
        if (!service.existsById(id)) {
            return ResponseEntity.ok(ErrorResponse.withMessage("Không tìm thấy danh mục"));
        }
        
        service.deleteById(id);
        return ResponseEntity.ok(SuccessResponse.withMessage(null, "Xóa danh mục thành công"));
    }
}
