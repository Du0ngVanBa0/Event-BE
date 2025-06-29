package DuongVanBao.event.dto.message;

import DuongVanBao.event.dto.response.DanhGiaResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DanhGiaMessage {
    private String type;
    private DanhGiaResponse data;
}
