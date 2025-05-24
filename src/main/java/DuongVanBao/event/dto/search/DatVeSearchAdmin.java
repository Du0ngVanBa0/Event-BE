package DuongVanBao.event.dto.search;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DatVeSearchAdmin {
    private String maDatVe;
    private String maKhachHang;
    private String trangThai;
    private Boolean hoatDong;
    private Boolean isExpired;
    private Integer fromMoney;
    private Integer toMoney;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
