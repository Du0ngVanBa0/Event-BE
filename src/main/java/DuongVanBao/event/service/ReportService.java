package DuongVanBao.event.service;

import DuongVanBao.event.dto.response.ReportResponse;
import DuongVanBao.event.repository.ReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportResponse getThongKeAllTime() {
        ReportResponse response = new ReportResponse();
        response.setTongSuKien(reportRepository.countTongSuKien());
        response.setTongSuKienChoDuyet(reportRepository.countSuKienChoDuyet());
        response.setTongNguoiDung(reportRepository.countTongNguoiDung());
        response.setDoanhThuThang(reportRepository.calculateDoanhThuAllTime());

        return response;
    }

    public ReportResponse getThongKeByDateRange(LocalDate tuNgay, LocalDate denNgay) {
        ReportResponse response = new ReportResponse();
        response.setTongSuKien(reportRepository.countTongSuKienByDateRange(tuNgay, denNgay));
        response.setTongSuKienChoDuyet(reportRepository.countSuKienChoDuyetByDateRange(tuNgay, denNgay));
        response.setTongNguoiDung(reportRepository.countTongNguoiDungByDateRange(tuNgay, denNgay));
        response.setDoanhThuThang(reportRepository.calculateDoanhThuByDateRange(tuNgay, denNgay));

        return response;
    }
}
