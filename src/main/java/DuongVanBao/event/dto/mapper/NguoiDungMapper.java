package DuongVanBao.event.dto.mapper;

import DuongVanBao.event.dto.response.NguoiDungResponse;
import DuongVanBao.event.model.entity.NguoiDung;
import org.springframework.stereotype.Component;

@Component
public class NguoiDungMapper {
    public NguoiDungResponse toResponse(NguoiDung nguoiDung) {
        return NguoiDungResponse.builder()
                .maNguoiDung(nguoiDung.getMaNguoiDung())
                .tenNguoiDung(nguoiDung.getTenNguoiDung())
                .tenHienThi(nguoiDung.getTenHienThi())
                .email(nguoiDung.getEmail())
                .vaiTro(nguoiDung.getVaiTro())
                .anhDaiDien(nguoiDung.getAnhDaiDien())
                .hoatDong(nguoiDung.isHoatDong())
                .ngayTao(nguoiDung.getNgayTao())
                .build();
    }
}
