package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.DanhMucSuKien;
import DuongVanBao.event.repository.DanhMucSuKienRepository;
import DuongVanBao.event.service.DanhMucSuKienService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DanhMucSuKienServiceImpl extends BaseServiceImpl<DanhMucSuKien, String> 
    implements DanhMucSuKienService {
    
    private final DanhMucSuKienRepository danhMucRepository;

    public DanhMucSuKienServiceImpl(DanhMucSuKienRepository repository) {
        super(repository);
        this.danhMucRepository = repository;
    }

    @Override
    public boolean existsByIds(List<String> ids) {
        return danhMucRepository.countByMaDanhMucIn(ids) == ids.size();
    }
}
