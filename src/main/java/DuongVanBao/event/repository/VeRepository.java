package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.LoaiVe;
import DuongVanBao.event.model.entity.Ve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeRepository extends JpaRepository<Ve, String> {
    List<Ve> findByDatVe(DatVe datVe);
    List<Ve> findByLoaiVe(LoaiVe loaiVe);
    long countByLoaiVe(LoaiVe loaiVe);
    List<Ve> findByLoaiVe_MaLoaiVe(String maLoaiVe);
    void deleteByDatVe(DatVe datVe);
}