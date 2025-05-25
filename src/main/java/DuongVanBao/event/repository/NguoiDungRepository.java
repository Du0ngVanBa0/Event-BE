package DuongVanBao.event.repository;

import DuongVanBao.event.dto.request.NguoiDungUpdateRequest;
import DuongVanBao.event.dto.response.NguoiDungResponse;
import DuongVanBao.event.enums.Role;
import DuongVanBao.event.model.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, String>, JpaSpecificationExecutor<NguoiDung> {
    Optional<NguoiDung> findByMaNguoiDung(String maNguoiDung);
    Optional<NguoiDung> findByEmailAndHoatDong(String email, boolean hoatDong);
}
