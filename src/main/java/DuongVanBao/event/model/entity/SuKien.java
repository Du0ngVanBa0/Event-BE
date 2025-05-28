package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "SUKIEN")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"diaDiem", "nguoiToChuc", "danhMucs", "khuVucs"})
@ToString(callSuper = true, exclude = {"diaDiem", "nguoiToChuc", "danhMucs", "khuVucs"})
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
    private LocalDateTime thoiGianBatDau;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime ngayMoBanVe;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime ngayDongBanVe;

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

    @OneToMany(mappedBy = "suKien")
    private Set<KhuVuc> khuVucs;
}