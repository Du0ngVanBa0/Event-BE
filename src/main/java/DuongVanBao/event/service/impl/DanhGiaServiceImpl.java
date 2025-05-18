package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.DanhGia;
import DuongVanBao.event.model.entity.SuKien;
import DuongVanBao.event.repository.DanhGiaRepository;
import DuongVanBao.event.service.DanhGiaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DanhGiaServiceImpl extends BaseServiceImpl<DanhGia, String> implements DanhGiaService {
    private final DanhGiaRepository danhGiaRepository;

    public DanhGiaServiceImpl(DanhGiaRepository repository) {
        super(repository);
        this.danhGiaRepository = repository;
    }

    @Override
    public Page<DanhGia> findBySuKien(SuKien suKien, Pageable pageable) {
        return danhGiaRepository.findBySuKien(suKien, pageable);
    }

    @Override
    public boolean existsByNguoiDungAndSuKien(String maNguoiDung, String maSuKien) {
        return danhGiaRepository.existsByNguoiDungMaNguoiDungAndSuKienMaSuKien(maNguoiDung, maSuKien);
    }
}