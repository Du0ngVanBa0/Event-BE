package DuongVanBao.event.model.entity;

import DuongVanBao.event.enums.Role;
import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "NGUOIDUNG")
@EqualsAndHashCode(callSuper = true)
public class NguoiDung extends BaseModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maNguoiDung;

    @Column(unique = true, nullable = false)
    private String tenNguoiDung;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String matKhau;

    @Enumerated(EnumType.STRING)
    private Role vaiTro;

    private String tenHienThi;
    private String anhDaiDien;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + vaiTro.name()));
    }

    @Override
    public String getPassword() {
        return matKhau;
    }

    @Override
    public String getUsername() {
        return email;
    }

}
