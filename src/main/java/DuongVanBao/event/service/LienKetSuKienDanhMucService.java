package DuongVanBao.event.service;

import DuongVanBao.event.model.LienKetSuKienDanhMucId;
import DuongVanBao.event.model.entity.LienKetSuKienDanhMuc;

import java.util.List;

public interface LienKetSuKienDanhMucService extends BaseService<LienKetSuKienDanhMuc, LienKetSuKienDanhMucId> {
    void deleteBySuKienId(String maSuKien);
    List<LienKetSuKienDanhMuc> findBySuKienId(String maSuKien);
}