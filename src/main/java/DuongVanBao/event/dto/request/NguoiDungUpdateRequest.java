package DuongVanBao.event.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NguoiDungUpdateRequest {
    private String tenNguoiDung;
    private String tenHienThi;
    private String email;
    private String matKhau;
    private MultipartFile anhDaiDien;
    private Boolean hoatDong;
}
