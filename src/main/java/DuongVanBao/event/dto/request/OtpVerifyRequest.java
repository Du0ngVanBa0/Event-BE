package DuongVanBao.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {
    @NotBlank(message = "Mã OTP không được để trống")
    private String maOtp;

    @NotBlank(message = "Mã xác thực không được để trống")
    private String maXacThuc;
}