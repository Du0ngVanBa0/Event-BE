package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatVeRepository extends JpaRepository<DatVe, String>, JpaSpecificationExecutor<DatVe> {
    List<DatVe> findByKhachHang(NguoiDung khachHang);
}