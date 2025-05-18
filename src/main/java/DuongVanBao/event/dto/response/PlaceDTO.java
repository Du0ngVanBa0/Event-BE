package DuongVanBao.event.dto.response;

import lombok.Data;
import java.util.Set;

@Data
public class PlaceDTO {
    private String maTinhThanh;
    private String tenTinhThanh;
    private Set<QuanHuyenDTO> quanHuyens;

    @Data
    public static class QuanHuyenDTO {
        private String maQuanHuyen;
        private String tenQuanHuyen;
        private Set<PhuongXaDTO> phuongXas;
    }

    @Data
    public static class PhuongXaDTO {
        private String maPhuongXa;
        private String tenPhuongXa;
    }
}