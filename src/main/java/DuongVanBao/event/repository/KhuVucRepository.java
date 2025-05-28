package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.KhuVuc;
import DuongVanBao.event.model.entity.SuKien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhuVucRepository extends JpaRepository<KhuVuc, String> {
    List<KhuVuc> findAllBySuKien(SuKien suKien);
    void deleteAllBySuKien(SuKien suKien);
}