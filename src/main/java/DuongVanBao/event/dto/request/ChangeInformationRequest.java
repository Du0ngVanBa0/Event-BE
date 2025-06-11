package DuongVanBao.event.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChangeInformationRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String hoVaTen;

    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String matKhauHienTai;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhauMoi;

    private MultipartFile anhDaiDien;
}
