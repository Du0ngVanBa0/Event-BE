package DuongVanBao.event.dto.response;

import DuongVanBao.event.model.BaseModel;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

@Data
public class EventResponse extends BaseModel {
    private String maSuKien;
    private String tieuDe;
    private String moTa;
    private Date thoiGianBatDau;
    private Date thoiGianKetThuc;
    private Date ngayMoBanVe;
    private Date ngayDongBanVe;
    private String anhBia;
    private boolean hoatDong;
    private DiaDiemResponse diaDiem;
    private List<DanhMucResponse> danhMucs;
    private List<LoaiVeResponse> loaiVes;

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