package DuongVanBao.event.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DanhGiaResponse {
    private String maDanhGia;
    private String noiDung;
    private Double diemDanhGia;
    private LocalDateTime ngayTao;
    private String hoTenNguoiDung;
}