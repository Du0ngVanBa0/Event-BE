package DuongVanBao.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LienKetSuKienDanhMucId implements Serializable {
    private String maSuKien;
    private String maDanhMuc;
}