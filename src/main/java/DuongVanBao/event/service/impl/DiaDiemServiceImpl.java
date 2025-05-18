package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.DiaDiem;
import DuongVanBao.event.repository.DiaDiemRepository;
import DuongVanBao.event.service.DiaDiemService;
import org.springframework.stereotype.Service;

@Service
public class DiaDiemServiceImpl extends BaseServiceImpl<DiaDiem, String> implements DiaDiemService {
    public DiaDiemServiceImpl(DiaDiemRepository repository) {
        super(repository);
    }
}