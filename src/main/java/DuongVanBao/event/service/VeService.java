package DuongVanBao.event.service;

import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.LoaiVe;
import DuongVanBao.event.model.entity.Ve;

import java.util.List;

public interface VeService extends BaseService<Ve, String> {
    List<Ve> findByDatVe(DatVe datVe);
    List<Ve> findByLoaiVe(LoaiVe loaiVe);
    long countByDatVe(LoaiVe loaiVe);
    List<Ve> findByLoaiVe_MaLoaiVe(String maLoaiVe);
    Integer calculateReservedTickets(String maLoaiVe);
    void deleteByDatVe(DatVe datVe);
}