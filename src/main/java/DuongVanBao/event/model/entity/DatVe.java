package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "DATVE")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"khachHang", "ves", "thanhToans"})
@ToString(callSuper = true, exclude = {"khachHang", "ves", "thanhToans"})
public class DatVe extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maDatVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhachHang", nullable = false)
    private NguoiDung khachHang;

    @Column(nullable = false)
    private BigDecimal tongTien;

    @Column(nullable = false)
    private String trangThai;

    @Column(nullable = false)
    private LocalDateTime thoiGianHetHan;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false)
    private Boolean hoatDong = false;

    @OneToMany(mappedBy = "datVe")
    private Set<Ve> ves;

    @OneToMany(mappedBy = "datVe")
    private Set<ThanhToan> thanhToans;
}