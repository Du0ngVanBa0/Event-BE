package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "THANHTOAN")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "datVe")
@ToString(callSuper = true, exclude = "datVe")
public class ThanhToan extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatVe", nullable = false)
    private DatVe datVe;

    @Column(nullable = false)
    private BigDecimal soTien;

    @Column(nullable = false)
    private String phuongThuc;

    @Column(nullable = false)
    private String trangThai;

    @Column(columnDefinition = "TEXT")
    private String phanHoiVNP;

    private LocalDateTime thoiGianThanhToan;
}