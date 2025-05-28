package DuongVanBao.event.dto.response;

import DuongVanBao.event.model.BaseModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Data
public class EventResponse extends BaseModel {
    private String maSuKien;
    private String tieuDe;
    private String moTa;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private LocalDateTime ngayMoBanVe;
    private LocalDateTime ngayDongBanVe;
    private String anhBia;
    private boolean hoatDong;
    private DiaDiemResponse diaDiem;
    private List<DanhMucResponse> danhMucs;
    private List<LoaiVeResponse> loaiVes;
    private List<KhuVucResponse> khuVucs;

    @Data
    public static class DiaDiemResponse {
        private String maDiaDiem;
        private String tenDiaDiem;
        private String maPhuongXa;
        private String tenPhuongXa;
        private String maQuanHuyen;
        private String tenQuanHuyen;
        private String maTinhThanh;
        private String tenTinhThanh;
    }

    @Data
    public static class DanhMucResponse {
        private String maDanhMuc;
        private String tenDanhMuc;
    }

    @Data
    public static class LoaiVeResponse {
        private String maLoaiVe;
        private String tenLoaiVe;
        private String moTa;
        private Integer soLuong;
        private Integer soLuongToiThieu;
        private Integer soLuongToiDa;
        private Integer veConLai;
        private BigDecimal giaTien;
    }
}