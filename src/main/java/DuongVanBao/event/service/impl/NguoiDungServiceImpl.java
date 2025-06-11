package DuongVanBao.event.service.impl;

import DuongVanBao.event.dto.mapper.NguoiDungMapper;
import DuongVanBao.event.dto.request.ChangeInformationRequest;
import DuongVanBao.event.dto.request.NguoiDungUpdateRequest;
import DuongVanBao.event.dto.response.NguoiDungResponse;
import DuongVanBao.event.enums.Role;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.repository.NguoiDungRepository;
import DuongVanBao.event.repository.OtpRepository;
import DuongVanBao.event.service.NguoiDungService;
import DuongVanBao.event.util.FileUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NguoiDungServiceImpl extends BaseServiceImpl<NguoiDung, String> implements NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final NguoiDungMapper nguoiDungMapper;
    private final OtpRepository otpRepository;
    private final FileUtil fileUtil;

    public NguoiDungServiceImpl(NguoiDungRepository repository, PasswordEncoder passwordEncoder, NguoiDungMapper nguoiDungMapper, OtpRepository otpRepository, FileUtil fileUtil) {
        super(repository);
        nguoiDungRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.nguoiDungMapper = nguoiDungMapper;
        this.otpRepository = otpRepository;
        this.fileUtil = fileUtil;
    }

    @Override
    public NguoiDungResponse findByMaNguoiDungResponse(String maNguoiDung) {
        NguoiDung nguoiDung = nguoiDungRepository.findByMaNguoiDung(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return nguoiDungMapper.toResponse(nguoiDung);
    }

    @Override
    public Page<NguoiDungResponse> findPageWithFilters(
            Boolean hoatDong,
            String tenNguoiDung,
            Role vaiTro,
            String tenHienThi,
            Pageable pageable) {

        Specification<NguoiDung> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hoatDong != null) {
                predicates.add(criteriaBuilder.equal(root.get("hoatDong"), hoatDong));
            }

            if (tenNguoiDung != null && !tenNguoiDung.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("tenNguoiDung")),
                        "%" + tenNguoiDung.toLowerCase() + "%"));
            }

            if (vaiTro != null) {
                predicates.add(criteriaBuilder.equal(root.get("vaiTro"), vaiTro));
            }

            if (tenHienThi != null && !tenHienThi.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("tenHienThi")),
                        "%" + tenHienThi.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return nguoiDungRepository.findAll(specification, pageable)
                .map(nguoiDungMapper::toResponse);
    }

    @Override
    public NguoiDungResponse updateUser(String maNguoiDung, NguoiDungUpdateRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findByMaNguoiDung(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (request.getTenNguoiDung() != null) {
            nguoiDung.setTenNguoiDung(request.getTenNguoiDung());
        }

        if (request.getTenHienThi() != null) {
            nguoiDung.setTenHienThi(request.getTenHienThi());
        }

        if (request.getEmail() != null) {
            nguoiDung.setEmail(request.getEmail());
        }

        if (request.getMatKhau() != null) {
            nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }

        if (request.getHoatDong() != null) {
            nguoiDung.setHoatDong(request.getHoatDong());
        }

        if (request.getAnhDaiDien() != null && !request.getAnhDaiDien().isEmpty()) {
            if (nguoiDung.getAnhDaiDien() != null && !nguoiDung.getAnhDaiDien().isEmpty()) {
                fileUtil.deleteFile(nguoiDung.getAnhDaiDien());
            }
            String fileName = fileUtil.saveFile(request.getAnhDaiDien());
            nguoiDung.setAnhDaiDien(fileName);
        }

        return nguoiDungMapper.toResponse(nguoiDungRepository.save(nguoiDung));
    }

    @Override
    public NguoiDungResponse updateRole(String maNguoiDung, Role vaiTro) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        nguoiDung.setVaiTro(vaiTro);
        return nguoiDungMapper.toResponse(nguoiDungRepository.save(nguoiDung));
    }

    @Override
    public void changeInformation(String maNguoiDung, ChangeInformationRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findByMaNguoiDung(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (request.getAnhDaiDien() != null && !request.getAnhDaiDien().isEmpty()) {
            if (nguoiDung.getAnhDaiDien() != null && !nguoiDung.getAnhDaiDien().isEmpty()) {
                fileUtil.deleteFile(nguoiDung.getAnhDaiDien());
            }
            String fileName = fileUtil.saveFile(request.getAnhDaiDien());
            nguoiDung.setAnhDaiDien(fileName);
        }

        nguoiDung.setTenHienThi(request.getHoVaTen());

        if (request.getMatKhauHienTai() != null && !request.getMatKhauHienTai().isEmpty()) {
            if (!passwordEncoder.matches(request.getMatKhauHienTai(), nguoiDung.getMatKhau())) {
                throw new RuntimeException("Mật khẩu hiện tại không đúng");
            }

            if (request.getMatKhauMoi() != null && !request.getMatKhauMoi().isEmpty()) {
                nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
            }
        }

        nguoiDungRepository.save(nguoiDung);
    }
    @Transactional
    @Override
    public void deleteUser(String maNguoiDung) {
        if (!nguoiDungRepository.existsById(maNguoiDung)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        otpRepository.deleteAllByNguoiDungMaNguoiDung(maNguoiDung);
        nguoiDungRepository.deleteById(maNguoiDung);
    }
}
