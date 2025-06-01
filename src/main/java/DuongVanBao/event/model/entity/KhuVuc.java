package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "KHUVUC")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"loaiVes", "suKien"})
@ToString(callSuper = true, exclude = {"loaiVes"})
public class KhuVuc extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maKhuVuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhuVucMau")
    private KhuVucMau template;

    @Column
    private String tenTuyChon;

    @Column(columnDefinition = "TEXT")
    private String moTaTuyChon;

    @Column(nullable = false)
    private String viTri;

    @Column
    private String mauSacTuyChon;

    @Column
    private Integer toaDoX;

    @Column
    private Integer toaDoY;

    @Column
    private Integer chieuRong;

    @Column
    private Integer chieuCao;

    @Column(nullable = false)
    private boolean hoatDong = true;

    @OneToMany(mappedBy = "khuVuc", cascade = CascadeType.ALL)
    private Set<LoaiVe> loaiVes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSuKien", nullable = false)
    private SuKien suKien;

    public String getTenHienThi() {
        return tenTuyChon != null && !tenTuyChon.trim().isEmpty()
                ? tenTuyChon : template.getTenKhuVuc();
    }

    public String getMauSacHienThi() {
        return mauSacTuyChon != null && !mauSacTuyChon.trim().isEmpty()
                ? mauSacTuyChon : template.getMauSac();
    }
}