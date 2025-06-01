package DuongVanBao.event.dto.request;

import lombok.Data;

@Data
public class KhuVucRequest {
    private String maTemplate;
    private String tenTuyChon;
    private String moTaTuyChon;
    private String mauSacTuyChon;
    private Integer toaDoX;
    private Integer toaDoY;
    private Integer chieuRong;
    private Integer chieuCao;
    private String viTri;
}