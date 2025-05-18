package DuongVanBao.event.service;

import DuongVanBao.event.model.entity.LoaiVe;
import DuongVanBao.event.model.entity.SuKien;

import java.util.List;

public interface LoaiVeService extends BaseService<LoaiVe, String> {
    List<LoaiVe> findBySuKien(SuKien suKien);
    void deleteAllBySuKien(SuKien suKien);
}