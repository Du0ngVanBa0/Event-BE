package DuongVanBao.event.service;

import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DatVeService extends BaseService<DatVe, String> {
    List<DatVe> findByKhachHang(NguoiDung khachHang);
    Page<DatVe> findAllFilter(Pageable pageable, String maNguoiDung, Boolean hoatDong, Boolean isTimeExpired);
}