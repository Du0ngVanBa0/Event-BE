package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.LoaiVe;
import DuongVanBao.event.model.entity.Ve;
import DuongVanBao.event.repository.VeRepository;
import DuongVanBao.event.service.VeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class VeServiceImpl extends BaseServiceImpl<Ve, String> implements VeService {
    private final VeRepository veRepository;

    public VeServiceImpl(VeRepository veRepository) {
        super(veRepository);
        this.veRepository = veRepository;
    }

    @Override
    public List<Ve> findByDatVe(DatVe datVe) {
        return veRepository.findByDatVe(datVe);
    }

    @Override
    public List<Ve> findByLoaiVe(LoaiVe loaiVe) {
        return veRepository.findByLoaiVe(loaiVe);
    }

    @Override
    public long countByDatVe(LoaiVe loaiVe) {
        return veRepository.countByLoaiVe(loaiVe);
    }

    @Override
    public List<Ve> findByLoaiVe_MaLoaiVe(String maLoaiVe) {
        return veRepository.findByLoaiVe_MaLoaiVe(maLoaiVe);
    }

    public Integer calculateReservedTickets(String maLoaiVe) {
        List<Ve> reservedVes = veRepository.findByLoaiVe_MaLoaiVe(maLoaiVe);

        // 1. Order is paid (hoatDong = true)
        // 2. OR order is pending but still within time limit (DatVe.hoatDong = false AND now < thoiGianHetHan)
        return (int) reservedVes.stream()
                .filter(ve -> {
                    DatVe datVe = ve.getDatVe();
                    return datVe.getHoatDong() || LocalDateTime.now().isBefore(datVe.getThoiGianHetHan());
                })
                .count();
    }

    @Override
    public void deleteByDatVe(DatVe datVe) {
        veRepository.deleteByDatVe(datVe);
    }
}