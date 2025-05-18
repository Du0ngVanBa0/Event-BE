package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VE")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"loaiVe", "datVe"})
@ToString(callSuper = true, exclude = {"loaiVe", "datVe"})
public class Ve extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLoaiVe", nullable = false)
    private LoaiVe loaiVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatVe", nullable = false)
    private DatVe datVe;

    @Column(nullable = false)
    private String trangThai;

    private LocalDateTime thoiGianKiemVe;
}