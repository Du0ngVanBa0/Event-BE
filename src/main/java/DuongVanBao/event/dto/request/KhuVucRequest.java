package DuongVanBao.event.dto.request;

import lombok.Data;

@Data
public class KhuVucRequest {
    private String tempId;
    private String tenKhuVuc;
    private String moTa;
    private String viTri;
    private String layoutData;
}