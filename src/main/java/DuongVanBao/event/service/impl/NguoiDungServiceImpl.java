package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.repository.NguoiDungRepository;
import DuongVanBao.event.service.NguoiDungService;
import org.springframework.stereotype.Service;

@Service
public class NguoiDungServiceImpl extends BaseServiceImpl<NguoiDung, String> implements NguoiDungService {
    public NguoiDungServiceImpl(NguoiDungRepository repository) {
        super(repository);
    }
}
