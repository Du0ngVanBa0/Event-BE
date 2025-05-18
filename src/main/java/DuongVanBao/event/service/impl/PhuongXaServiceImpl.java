package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.PhuongXa;
import DuongVanBao.event.repository.PhuongXaRepository;
import DuongVanBao.event.service.PhuongXaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhuongXaServiceImpl extends BaseServiceImpl<PhuongXa, String> implements PhuongXaService {
    public PhuongXaServiceImpl(PhuongXaRepository repository) {
        super(repository);
    }

    @Override
    public List<PhuongXa> findAll() {
        return super.findAll();
    }
}
