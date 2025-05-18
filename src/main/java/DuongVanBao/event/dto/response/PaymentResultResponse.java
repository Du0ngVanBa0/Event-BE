package DuongVanBao.event.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResultResponse {
    private boolean success;
    private String message;
    private String maDatVe;
    private String maThanhToan;
    private Long tongTien;
    private String maGiaoDich;
    private LocalDateTime ngayThanhToan;
}
