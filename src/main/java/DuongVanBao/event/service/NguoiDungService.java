package DuongVanBao.event.service;

import DuongVanBao.event.dto.request.ChangeInformationRequest;
import DuongVanBao.event.dto.request.NguoiDungUpdateRequest;
import DuongVanBao.event.dto.response.NguoiDungResponse;
import DuongVanBao.event.enums.Role;
import DuongVanBao.event.model.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NguoiDungService extends BaseService<NguoiDung, String> {
    NguoiDungResponse findByMaNguoiDungResponse(String maNguoiDung);
    NguoiDungResponse updateUser(String maNguoiDung, NguoiDungUpdateRequest request);
    NguoiDungResponse updateRole(String maNguoiDung, Role vaiTro);
    void deleteUser(String maNguoiDung);
    public void changeInformation(String maNguoiDung, ChangeInformationRequest request);
        Page<NguoiDungResponse> findPageWithFilters(
            Boolean hoatDong,
            String tenNguoiDung,
            Role vaiTro,
            String tenHienThi,
            Pageable pageable);
}