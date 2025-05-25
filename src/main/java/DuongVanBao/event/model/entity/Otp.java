package DuongVanBao.event.model.entity;

import DuongVanBao.event.enums.OtpType;
import DuongVanBao.event.model.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "OTP")
public class Otp extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maOtp;

    @Column(nullable = false, length = 8)
    private String maXacThuc;

    @Column(nullable = false)
    private LocalDateTime thoiGianTao;

    @Column(nullable = false)
    private LocalDateTime thoiGianHetHan;

    private LocalDateTime thoiGianXacThuc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpType loaiOtp;

    private boolean daXacThuc;

    @ManyToOne
    @JoinColumn(name = "maNguoiDung", nullable = false)
    private NguoiDung nguoiDung;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(thoiGianHetHan);
    }
}