package DuongVanBao.event.dto.request;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private String maDatVe;
    private String bankCode;
}
