package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.PhuongXa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhuongXaRepository extends JpaRepository<PhuongXa, String> {
}