package DuongVanBao.event.service;

import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.ThanhToan;

import java.util.List;

public interface ThanhToanService extends BaseService<ThanhToan, String> {
    List<ThanhToan> findByDatVe(DatVe datVe);
}