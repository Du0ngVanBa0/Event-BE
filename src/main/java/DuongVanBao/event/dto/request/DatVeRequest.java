package DuongVanBao.event.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class DatVeRequest {
    private List<ChiTietVe> chiTietDatVe;

    @Data
    public static class ChiTietVe {
        private String maLoaiVe;
        private int soLuong;
    }
}
