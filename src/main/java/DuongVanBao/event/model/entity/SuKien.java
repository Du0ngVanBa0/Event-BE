package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "SUKIEN")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"diaDiem", "nguoiToChuc", "danhMucs"})
@ToString(callSuper = true, exclude = {"diaDiem", "nguoiToChuc", "danhMucs"})
public class SuKien extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maSuKien;

    @Column(nullable = false)
    private String tieuDe;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date thoiGianBatDau;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date thoiGianKetThuc;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayMoBanVe;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayDongBanVe;

    private String anhBia;

    @Column(nullable = false)
    private boolean hoatDong = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDiaDiem", nullable = false)
    private DiaDiem diaDiem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNguoiToChuc", nullable = false)
    private NguoiDung nguoiToChuc;

    @OneToMany(mappedBy = "suKien")
    private Set<LienKetSuKienDanhMuc> danhMucs;
}