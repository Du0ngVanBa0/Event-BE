package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Table(name = "PHUONGXA")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "quanHuyen")
@ToString(callSuper = true, exclude = "quanHuyen")
public class PhuongXa extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maPhuongXa;

    @Column(nullable = false)
    private String tenPhuongXa;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "maQuanHuyen", nullable = false)
    private QuanHuyen quanHuyen;
}
