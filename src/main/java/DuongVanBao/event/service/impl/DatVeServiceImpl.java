package DuongVanBao.event.service.impl;

import DuongVanBao.event.dto.search.DatVeSearchAdmin;
import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.repository.DatVeRepository;
import DuongVanBao.event.service.DatVeService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DatVeServiceImpl extends BaseServiceImpl<DatVe, String> implements DatVeService {
    private final DatVeRepository datVeRepository;

    public DatVeServiceImpl(DatVeRepository repository) {
        super(repository);
        this.datVeRepository = repository;
    }

    @Override
    public List<DatVe> findByKhachHang(NguoiDung khachHang) {
        return datVeRepository.findByKhachHang(khachHang);
    }

    @Override
    public Page<DatVe> findAllFilter(Pageable pageable, String maNguoiDung, Boolean hoatDong, Boolean isTimeExpired) {
        Specification<DatVe> specification = Specification.where(null);
        if (maNguoiDung != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("khachHang").get("maNguoiDung"), maNguoiDung));
        }
        if (hoatDong != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hoatDong"),hoatDong));
        }
        if (isTimeExpired != null) {
            if (!isTimeExpired) {
                specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("thoiGianHetHan"), LocalDateTime.now()));
            } else {
                specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("thoiGianHetHan"), LocalDateTime.now()));
            }
        }

        return datVeRepository.findAll(specification, pageable);
    }

    @Override
    public Page<DatVe> findPageFilterAdmin(Pageable pageable, DatVeSearchAdmin datVeSearchAdmin) {
        Specification<DatVe> specification = Specification.where(null);
        if (datVeSearchAdmin.getMaDatVe() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("maDatVe"), datVeSearchAdmin.getMaDatVe()));
        }
        if (datVeSearchAdmin.getMaKhachHang() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.join("khachHang").get("maNguoiDung"), "%" + datVeSearchAdmin.getMaKhachHang() + "%"));
        }
        if (datVeSearchAdmin.getHoatDong() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hoatDong"),datVeSearchAdmin.getHoatDong()));
        }
        if (datVeSearchAdmin.getIsExpired() != null) {
            if (!datVeSearchAdmin.getIsExpired()) {
                specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("thoiGianHetHan"), LocalDateTime.now()));
            } else {
                specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("thoiGianHetHan"), LocalDateTime.now()));
            }
        }
        if (datVeSearchAdmin.getFromDate() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("ngayTao"), datVeSearchAdmin.getFromDate()));
        }
        if (datVeSearchAdmin.getToDate() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("ngayTao"), datVeSearchAdmin.getToDate()));
        }
        if (datVeSearchAdmin.getFromMoney() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("tongTien"), datVeSearchAdmin.getFromMoney()));
        }
        if (datVeSearchAdmin.getToMoney() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("tongTien"), datVeSearchAdmin.getToMoney()));
        }
        return datVeRepository.findAll(specification, pageable);
    }

}