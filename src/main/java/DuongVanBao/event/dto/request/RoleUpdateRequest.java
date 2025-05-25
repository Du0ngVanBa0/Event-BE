package DuongVanBao.event.dto.request;

import DuongVanBao.event.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    @NotNull(message = "Vai trò không được để trống")
    private Role vaiTro;
}
