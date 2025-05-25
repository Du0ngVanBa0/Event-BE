package DuongVanBao.event.dto.response;

import DuongVanBao.event.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NguoiDungResponse {
    private String maNguoiDung;
    private String tenNguoiDung;
    private String tenHienThi;
    private String email;
    private Role vaiTro;
    private String anhDaiDien;
    private boolean hoatDong;
    private LocalDateTime ngayTao;
}