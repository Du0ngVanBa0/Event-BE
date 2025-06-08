package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.DanhMucSuKien;
import DuongVanBao.event.model.entity.LienKetSuKienDanhMuc;
import DuongVanBao.event.model.entity.SuKien;
import DuongVanBao.event.repository.DanhMucSuKienRepository;
import DuongVanBao.event.repository.LienKetSuKienDanhMucRepository;
import DuongVanBao.event.repository.SuKienRepository;
import DuongVanBao.event.service.SuKienService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SuKienServiceImpl extends BaseServiceImpl<SuKien, String> implements SuKienService {
    private final SuKienRepository suKienRepository;
    private final LienKetSuKienDanhMucRepository lienKetSuKienDanhMucRepository;
    private final DanhMucSuKienRepository danhMucRepository;

    public SuKienServiceImpl(SuKienRepository repository, SuKienRepository suKienRepository, LienKetSuKienDanhMucRepository lienKetSuKienDanhMucRepository, DanhMucSuKienRepository danhMucRepository) {
        super(repository);
        this.suKienRepository = suKienRepository;
        this.lienKetSuKienDanhMucRepository = lienKetSuKienDanhMucRepository;
        this.danhMucRepository = danhMucRepository;
    }

    @Override
    public Page<SuKien> findAllByHoatDong(boolean hoatDong, Pageable pageable) {
        return suKienRepository.findAllByHoatDong(hoatDong, pageable);
    }

    @Override
    public Page<SuKien> findByNguoiToChuc(String maNguoiDung, Pageable pageable) {
        return suKienRepository.findByNguoiToChucMaNguoiDung(maNguoiDung, pageable);
    }

    @Override
    public Page<SuKien> findByNguoiToChucAndHoatDong(String maNguoiDung, boolean hoatDong, Pageable pageable) {
        return suKienRepository.findByNguoiToChucMaNguoiDungAndHoatDong(maNguoiDung, hoatDong, pageable);
    }

    @Override
    public Page<SuKien> findPageSuKien(String maDanhMuc, Boolean hoatDong, Pageable pageable) {
        Specification<SuKien> specification = Specification.where(null);
        if (maDanhMuc != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("danhMucs").get("maDanhMuc"), maDanhMuc));
        }
        if (hoatDong != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hoatDong"), hoatDong));
        }

        return suKienRepository.findAll(specification, pageable);
    }

    @Override
    public void updateDanhMucLinks(SuKien suKien, String[] maDanhMucs) {
        lienKetSuKienDanhMucRepository.deleteBySuKienId(suKien.getMaSuKien());

        for (String maDanhMuc : maDanhMucs) {
            if (maDanhMuc != null && !maDanhMuc.isEmpty()) {
                DanhMucSuKien danhMuc = danhMucRepository.findById(maDanhMuc)
                        .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

                LienKetSuKienDanhMuc lienKet = new LienKetSuKienDanhMuc();
                lienKet.setMaSuKien(suKien.getMaSuKien());
                lienKet.setMaDanhMuc(maDanhMuc);
                lienKet.setSuKien(suKien);
                lienKet.setDanhMuc(danhMuc);
                lienKetSuKienDanhMucRepository.save(lienKet);            }
        }
    }
}