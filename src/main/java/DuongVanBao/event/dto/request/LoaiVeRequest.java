package DuongVanBao.event.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoaiVeRequest {
    private String tenLoaiVe;
    private String moTa;
    private Integer soLuong;
    private Integer soLuongToiThieu;
    private Integer soLuongToiDa;
    private BigDecimal giaTien;
}