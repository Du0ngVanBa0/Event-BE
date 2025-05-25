package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {
    Optional<Otp> findByMaOtp(String maOtp);
    void deleteAllByNguoiDungMaNguoiDung(String maNguoiDung);
}