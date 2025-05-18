package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.DiaDiem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaDiemRepository extends JpaRepository<DiaDiem, String> {
}