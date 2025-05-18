package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "LOAIVE")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"suKien", "ves"})
@ToString(callSuper = true, exclude = {"suKien", "ves"})
public class LoaiVe extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maLoaiVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSuKien", nullable = false)
    private SuKien suKien;

    @Column(nullable = false)
    private String tenLoaiVe;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(nullable = false)
    private Integer soLuong;

    @Column(nullable = false)
    private Integer soLuongToiThieu;

    @Column(nullable = false)
    private Integer soLuongToiDa;

    @Column(nullable = false)
    private BigDecimal giaTien;

    @OneToMany(mappedBy = "loaiVe")
    private Set<Ve> ves;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhuVuc", nullable = false)
    private KhuVuc khuVuc;
}