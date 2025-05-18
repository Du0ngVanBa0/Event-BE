package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "TINHTHANH")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "quanHuyens")
@ToString(callSuper = true, exclude = "quanHuyens")
public class TinhThanh extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maTinhThanh;

    @Column(nullable = false)
    private String tenTinhThanh;

    @JsonIgnoreProperties("tinhThanh")
    @OneToMany(mappedBy = "tinhThanh", fetch = FetchType.LAZY)
    private Set<QuanHuyen> quanHuyens = new HashSet<>();
}
