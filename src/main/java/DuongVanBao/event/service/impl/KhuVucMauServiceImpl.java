package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.KhuVucMau;
import DuongVanBao.event.repository.KhuVucMauRepository;
import DuongVanBao.event.service.KhuVucMauService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhuVucMauServiceImpl extends BaseServiceImpl<KhuVucMau, String> implements KhuVucMauService {
    private final KhuVucMauRepository khuVucMauRepository;

    public KhuVucMauServiceImpl(KhuVucMauRepository repository) {
        super(repository);
        this.khuVucMauRepository = repository;
    }

    @Override
    public List<KhuVucMau> findByHoatDongTrueOrderByThuTuHienThi() {
        return khuVucMauRepository.findByHoatDongTrueOrderByThuTuHienThi();
    }

    @Override
    public List<KhuVucMau> findByTenKhuVucContainingAndHoatDongTrue(String tenKhuVuc) {
        return khuVucMauRepository.findByTenKhuVucContainingAndHoatDongTrue(tenKhuVuc);
    }
}
