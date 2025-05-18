package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import DuongVanBao.event.model.LienKetSuKienDanhMucId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "LIENKETSUKIENDANHMUC")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"suKien", "danhMuc"})
@ToString(callSuper = true, exclude = {"suKien", "danhMuc"})
@IdClass(LienKetSuKienDanhMucId.class)
public class LienKetSuKienDanhMuc extends BaseModel {
    @Id
    @Column(name = "maSuKien")
    private String maSuKien;

    @Id
    @Column(name = "maDanhMuc")
    private String maDanhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSuKien", insertable = false, updatable = false)
    private SuKien suKien;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maDanhMuc", insertable = false, updatable = false)
    private DanhMucSuKien danhMuc;
}