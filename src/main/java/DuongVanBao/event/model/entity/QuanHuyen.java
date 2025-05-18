package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Data
@Entity
@Table(name = "QUANHUYEN")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"tinhThanh", "phuongXas"})
@ToString(callSuper = true, exclude = {"tinhThanh", "phuongXas"})
public class QuanHuyen extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maQuanHuyen;

    @Column(nullable = false)
    private String tenQuanHuyen;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "maTinhThanh", nullable = false)
    private TinhThanh tinhThanh;

    @JsonManagedReference
    @OneToMany(mappedBy = "quanHuyen", fetch = FetchType.EAGER)
    private Set<PhuongXa> phuongXas;
}
