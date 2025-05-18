package DuongVanBao.event.service;

import DuongVanBao.event.model.entity.DanhGia;
import DuongVanBao.event.model.entity.SuKien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DanhGiaService extends BaseService<DanhGia, String> {
    Page<DanhGia> findBySuKien(SuKien suKien, Pageable pageable);
    boolean existsByNguoiDungAndSuKien(String maNguoiDung, String maSuKien);
}