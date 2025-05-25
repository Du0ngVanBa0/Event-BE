package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.NguoiDungUpdateRequest;
import DuongVanBao.event.dto.request.RoleUpdateRequest;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.enums.Role;
import DuongVanBao.event.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final NguoiDungService nguoiDungService;

    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getAllUsers(
            @RequestParam(required = false) Boolean hoatDong,
            @RequestParam(required = false) String tenNguoiDung,
            @RequestParam(required = false) Role vaiTro,
            @RequestParam(required = false) String tenHienThi,
            Pageable pageable) {
        return ResponseEntity.ok(SuccessResponse.withData(
                nguoiDungService.findPageWithFilters(hoatDong, tenNguoiDung, vaiTro, tenHienThi, pageable)
        ));
    }

    @GetMapping("/{maNguoiDung}")
    public ResponseEntity<SuccessResponse<?>> getUserById(@PathVariable String maNguoiDung) {
        return ResponseEntity.ok(SuccessResponse.withData(
                nguoiDungService.findByMaNguoiDungResponse(maNguoiDung)
        ));
    }

    @PutMapping("/{maNguoiDung}")
    public ResponseEntity<SuccessResponse<?>> updateUser(
            @PathVariable String maNguoiDung,
            @ModelAttribute NguoiDungUpdateRequest request) {
        return ResponseEntity.ok(SuccessResponse.withMessage(
                nguoiDungService.updateUser(maNguoiDung, request),
                "Cập nhật thông tin người dùng thành công"
        ));
    }

    @PutMapping("/role/{maNguoiDung}")
    public ResponseEntity<SuccessResponse<?>> updateRole(
            @PathVariable String maNguoiDung,
            @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(SuccessResponse.withMessage(
                nguoiDungService.updateRole(maNguoiDung, request.getVaiTro()),
                "Cập nhật quyền người dùng thành công"
        ));
    }

    @DeleteMapping("/{maNguoiDung}")
    public ResponseEntity<SuccessResponse<?>> deleteUser(@PathVariable String maNguoiDung) {
        nguoiDungService.deleteUser(maNguoiDung);
        return ResponseEntity.ok(SuccessResponse.withMessage(
                null,
                "Xóa người dùng thành công"
        ));
    }
}