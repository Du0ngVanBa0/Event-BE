package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {
    Optional<NguoiDung> findByMaNguoiDung(String maNguoiDung);
    Optional<NguoiDung> findByEmailAndHoatDong(String email, boolean hoatDong);
}
