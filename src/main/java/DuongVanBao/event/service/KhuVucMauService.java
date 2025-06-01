package DuongVanBao.event.service;

import DuongVanBao.event.model.entity.KhuVucMau;

import java.util.List;

public interface KhuVucMauService extends BaseService<KhuVucMau, String> {
    List<KhuVucMau> findByHoatDongTrueOrderByThuTuHienThi();
    List<KhuVucMau> findByTenKhuVucContainingAndHoatDongTrue(String tenKhuVuc);
}
