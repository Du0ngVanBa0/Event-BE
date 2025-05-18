package DuongVanBao.event.service.impl;

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
}