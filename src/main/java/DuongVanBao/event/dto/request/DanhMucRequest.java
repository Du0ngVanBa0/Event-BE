package DuongVanBao.event.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DanhMucRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String tenDanhMuc;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String moTa;

    private boolean hoatDong;
}
