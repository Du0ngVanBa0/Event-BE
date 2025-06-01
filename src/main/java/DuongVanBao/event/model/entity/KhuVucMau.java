package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class KhuVucMau extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maKhuVucMau;

    @Column(nullable = false, unique = true)
    private String tenKhuVuc;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(nullable = false)
    private String mauSac; // Hex color

    @Column(nullable = false)
    private String hinhDang; // RECTANGLE, CIRCLE, TRIANGLE

    @Column(nullable = false)
    private Integer thuTuHienThi;

    @Column(nullable = false)
    private boolean hoatDong = true;

    @Column
    private Integer toaDoXMacDinh;

    @Column
    private Integer toaDoYMacDinh;

    @Column
    private Integer chieuRongMacDinh;

    @Column
    private Integer chieuCaoMacDinh;
}
