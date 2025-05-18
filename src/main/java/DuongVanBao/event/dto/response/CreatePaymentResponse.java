package DuongVanBao.event.dto.response;

import lombok.Data;

@Data
public class CreatePaymentResponse {
    private String paymentUrl;
    private String maThanhToan;
    private String maDatVe;
}
