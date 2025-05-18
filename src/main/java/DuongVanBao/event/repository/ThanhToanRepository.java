package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, String> {
    List<ThanhToan> findByDatVe(DatVe datVe);

    @Query("SELECT t FROM ThanhToan t WHERE t.datVe = ?1 AND t.phuongThuc = ?2 ORDER BY t.thoiGianThanhToan DESC")
    List<ThanhToan> findByDatVeAndPhuongThucOrderByTimeDesc(DatVe datVe, String phuongThuc);

    default Optional<ThanhToan> findLatestByDatVeAndPhuongThuc(DatVe datVe, String phuongThuc) {
        List<ThanhToan> thanhToans = findByDatVeAndPhuongThucOrderByTimeDesc(datVe, phuongThuc);
        return thanhToans.isEmpty() ? Optional.empty() : Optional.of(thanhToans.get(0));
    }

    void deleteByDatVe(DatVe datVe);
}