package DuongVanBao.event.service;

import DuongVanBao.event.model.entity.SuKien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SuKienService extends BaseService<SuKien, String> {
    Page<SuKien> findAllByHoatDong(boolean hoatDong, Pageable pageable);
    Page<SuKien> findByNguoiToChuc(String maNguoiDung, Pageable pageable);
    Page<SuKien> findByNguoiToChucAndHoatDong(String maNguoiDung, boolean hoatDong, Pageable pageable);
    Page<SuKien> findPageSuKien(String maDanhMuc, Boolean hoatDong, Pageable pageable);
    void updateDanhMucLinks(SuKien suKien, String[] maDanhMucs);
}