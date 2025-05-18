package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.LoaiVe;
import DuongVanBao.event.model.entity.SuKien;
import DuongVanBao.event.repository.LoaiVeRepository;
import DuongVanBao.event.service.LoaiVeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class LoaiVeServiceImpl extends BaseServiceImpl<LoaiVe, String> implements LoaiVeService {
    private final LoaiVeRepository loaiVeRepository;

    public LoaiVeServiceImpl(LoaiVeRepository repository) {
        super(repository);
        this.loaiVeRepository = repository;
    }

    @Override
    public List<LoaiVe> findBySuKien(SuKien suKien) {
        return loaiVeRepository.findBySuKien(suKien);
    }

    @Override
    public void deleteAllBySuKien(SuKien suKien) {
        loaiVeRepository.deleteAllBySuKien(suKien);
    }
}