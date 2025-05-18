package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DIADIEM")
@Getter
@Setter
public class DiaDiem extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maDiaDiem;

    @Column(nullable = false)
    private String tenDiaDiem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhuongXa", nullable = false)
    private PhuongXa phuongXa;
}