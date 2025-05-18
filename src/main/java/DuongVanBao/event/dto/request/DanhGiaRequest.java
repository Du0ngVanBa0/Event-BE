package DuongVanBao.event.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DanhGiaRequest {
    @NotBlank(message = "Nội dung đánh giá không được để trống")
    private String noiDung;

    @NotNull(message = "Điểm đánh giá không được để trống")
    @Min(value = 1, message = "Điểm đánh giá phải từ 1 đến 5")
    @Max(value = 5, message = "Điểm đánh giá phải từ 1 đến 5")
    private Double diemDanhGia;
}