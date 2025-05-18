package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.DanhMucSuKien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhMucSuKienRepository extends JpaRepository<DanhMucSuKien, String> {
    @Query("SELECT COUNT(d) FROM DanhMucSuKien d WHERE d.maDanhMuc IN :ids")
    long countByMaDanhMucIn(List<String> ids);
}
