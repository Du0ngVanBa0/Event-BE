package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.LienKetSuKienDanhMucId;
import DuongVanBao.event.model.entity.LienKetSuKienDanhMuc;
import DuongVanBao.event.repository.LienKetSuKienDanhMucRepository;
import DuongVanBao.event.service.LienKetSuKienDanhMucService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LienKetSuKienDanhMucServiceImpl extends BaseServiceImpl<LienKetSuKienDanhMuc, LienKetSuKienDanhMucId> 
    implements LienKetSuKienDanhMucService {
    
    private final LienKetSuKienDanhMucRepository lienKetRepository;

    public LienKetSuKienDanhMucServiceImpl(LienKetSuKienDanhMucRepository repository) {
        super(repository);
        this.lienKetRepository = repository;
    }

    @Override
    public void deleteBySuKienId(String maSuKien) {
        lienKetRepository.deleteBySuKienId(maSuKien);
    }

    @Override
    public List<LienKetSuKienDanhMuc> findBySuKienId(String maSuKien){
        return lienKetRepository.findBySuKienId(maSuKien);
    }

}