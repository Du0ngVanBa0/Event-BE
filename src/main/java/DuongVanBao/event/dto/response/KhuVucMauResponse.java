package DuongVanBao.event.dto.response;

import lombok.Data;

@Data
public class KhuVucMauResponse {
    private String maTemplate;
    private String tenKhuVuc;
    private String moTa;
    private String mauSac;
    private String hinhDang;
    private Integer thuTuHienThi;
    private boolean hoatDong;
    private Integer toaDoXMacDinh;
    private Integer toaDoYMacDinh;
    private Integer chieuRongMacDinh;
    private Integer chieuCaoMacDinh;
}
