package DuongVanBao.event.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Tên người dùng không được để trống")
    private String tenNguoiDung;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;

    @Size(min = 5, max = 20, message = "Tên hiển thị phải từ 5 đến 20 ký tự")
    private String tenHienThi;
}
