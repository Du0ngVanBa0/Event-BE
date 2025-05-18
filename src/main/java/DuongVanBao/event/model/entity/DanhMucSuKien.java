package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "DANHMUCSUKIEN")
public class DanhMucSuKien extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maDanhMuc;

    @Column(nullable = false)
    private String tenDanhMuc;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(nullable = false)
    private boolean hoatDong;
}
