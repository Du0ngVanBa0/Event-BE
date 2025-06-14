package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.ChangeInformationRequest;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.service.NguoiDungService;
import DuongVanBao.event.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class NguoiDungController {
    private final NguoiDungService nguoiDungService;

    public NguoiDungController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    @PostMapping("/change-information")
    public ResponseEntity<?> changeInformation(@ModelAttribute ChangeInformationRequest request) {
        NguoiDung nguoiDung = SecurityUtils.getCurrentUser();
        nguoiDung =  nguoiDungService.changeInformation(nguoiDung.getMaNguoiDung(), request);

        return ResponseEntity.ok(SuccessResponse.withMessage(
                nguoiDung,
                "Cập nhật thông tin thành công"
        ));
    }
}
