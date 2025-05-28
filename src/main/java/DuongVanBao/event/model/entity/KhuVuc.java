package DuongVanBao.event.model.entity;

import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "KHUVUC")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"loaiVes", "suKien"})
@ToString(callSuper = true, exclude = {"loaiVes"})
public class KhuVuc extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maKhuVuc;

    @Column(nullable = false)
    private String tenKhuVuc;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(nullable = false)
    private String viTri;

    @Column(columnDefinition = "TEXT")
    private String layoutData;

    @OneToMany(mappedBy = "khuVuc")
    private Set<LoaiVe> loaiVes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSuKien")
    private SuKien suKien;
}