package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.TinhThanh;
import DuongVanBao.event.repository.TinhThanhRepository;
import DuongVanBao.event.service.TinhThanhService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TinhThanhServiceImpl extends BaseServiceImpl<TinhThanh, String> implements TinhThanhService {
    public TinhThanhServiceImpl(TinhThanhRepository repository) {
        super(repository);
    }

    @Override
    public List<TinhThanh> findAll() {
        return super.findAll();
    }
}
