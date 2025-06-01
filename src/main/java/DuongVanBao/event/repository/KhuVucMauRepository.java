package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.KhuVucMau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KhuVucMauRepository extends JpaRepository<KhuVucMau, String> {
    @Query("SELECT k FROM KhuVucMau k WHERE k.hoatDong = true ORDER BY k.thuTuHienThi ASC")
    List<KhuVucMau> findByHoatDongTrueOrderByThuTuHienThi();

    @Query("SELECT k FROM KhuVucMau k WHERE k.hoatDong = true AND k.tenKhuVuc LIKE %?1%")
    List<KhuVucMau> findByTenKhuVucContainingAndHoatDongTrue(String tenKhuVuc);
}
