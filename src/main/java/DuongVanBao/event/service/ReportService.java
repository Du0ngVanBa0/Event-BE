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

    public ReportResponse getThongKe(Integer month, Integer year) {
        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        ReportResponse response = new ReportResponse();
        response.setTongSuKien(reportRepository.countTongSuKien());
        response.setTongSuKienChoDuyet(reportRepository.countSuKienChoDuyet());
        response.setTongNguoiDung(reportRepository.countTongNguoiDung());
        response.setDoanhThuThang(reportRepository.calculateDoanhThuThang(month, year));

        List<ReportResponse.DanhMucPhoBien> danhMucPhoBien = reportRepository.findDanhMucPhoBien(month, year);
        response.setDanhMucPhoBien(danhMucPhoBien);

        List<ReportResponse.SuKienHot> suKienHot = reportRepository.findSuKienHotThang(month, year);
        response.setSuKienHot(suKienHot);

        return response;
    }
}
