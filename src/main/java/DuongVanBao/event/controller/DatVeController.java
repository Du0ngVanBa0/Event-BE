package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.DatVeRequest;
import DuongVanBao.event.dto.response.DatVeResponse;
import DuongVanBao.event.dto.response.ErrorResponse;
import DuongVanBao.event.dto.response.EventResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.LoaiVe;
import DuongVanBao.event.model.entity.SuKien;
import DuongVanBao.event.model.entity.Ve;
import DuongVanBao.event.service.DatVeService;
import DuongVanBao.event.service.LoaiVeService;
import DuongVanBao.event.service.VeService;
import DuongVanBao.event.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/ticket-holder")
@RestController
@RequiredArgsConstructor
public class DatVeController {
    private final DatVeService datVeService;
    private final LoaiVeService loaiVeService;
    private final VeService veService;
    private static final int BOOKING_EXPIRATION_MINUTES = 15;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("")
    @Transactional
    public ResponseEntity<?> createPlaceholder(@RequestBody DatVeRequest request) {
        if (request.getChiTietDatVe() == null || request.getChiTietDatVe().isEmpty()) {
            throw new RuntimeException("Cần chọn ít nhất một vé");
        }

        BigDecimal tongTien = BigDecimal.ZERO;
        Map<String, Integer> ticketCounts = new HashMap<>();

        for (DatVeRequest.ChiTietVe chiTiet : request.getChiTietDatVe()) {
            ticketCounts.put(chiTiet.getMaLoaiVe(),
                    ticketCounts.getOrDefault(chiTiet.getMaLoaiVe(), 0) + chiTiet.getSoLuong());
        }

        for (Map.Entry<String, Integer> entry : ticketCounts.entrySet()) {
            LoaiVe loaiVe = loaiVeService.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Loại vé không tồn tại"));

            if (!loaiVe.getSuKien().isHoatDong()) {
                throw new RuntimeException("Sự kiện chưa được phê duyệt");
            }

            LocalDateTime now = LocalDateTime.now();
            if (loaiVe.getSuKien().getNgayMoBanVe() != null &&
                    now.isBefore(loaiVe.getSuKien().getNgayMoBanVe().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())) {
                throw new RuntimeException("Chưa đến thời gian bán vé");
            }

            if (loaiVe.getSuKien().getNgayDongBanVe() != null &&
                    now.isAfter(loaiVe.getSuKien().getNgayDongBanVe().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())) {
                throw new RuntimeException("Đã hết thời gian bán vé");
            }

            if (veService.calculateReservedTickets(loaiVe.getMaLoaiVe()) + entry.getValue() > loaiVe.getSoLuong()) {
                throw new RuntimeException("Loại vé " + loaiVe.getTenLoaiVe() + " không đủ số lượng");
            }

            tongTien = tongTien.add(loaiVe.getGiaTien().multiply(new BigDecimal(entry.getValue())));
        }

        DatVe datVe = new DatVe();
        datVe.setKhachHang(SecurityUtils.getCurrentUser());
        datVe.setTongTien(tongTien);
        datVe.setTrangThai("CHO_THANH_TOAN");
        datVe.setThoiGianHetHan(LocalDateTime.now().plusMinutes(BOOKING_EXPIRATION_MINUTES));
        datVe.setHoatDong(false);
        datVeService.save(datVe);

        for (DatVeRequest.ChiTietVe chiTiet : request.getChiTietDatVe()) {
            LoaiVe loaiVe = loaiVeService.findById(chiTiet.getMaLoaiVe())
                    .orElseThrow(() -> new RuntimeException("Loại vé không tồn tại"));

            for (int i = 0; i < chiTiet.getSoLuong(); i++) {
                Ve ve = new Ve();
                ve.setLoaiVe(loaiVe);
                ve.setDatVe(datVe);
                ve.setTrangThai("CHO_THANH_TOAN");
                veService.save(ve);
            }
        }

        return ResponseEntity.ok(SuccessResponse.withData(toDatVeResponse(datVe)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/mine")
    public ResponseEntity<?> mine(Pageable pageable,
                                              @RequestParam(required = false) String trangThai) {
        Page<DatVeResponse> responses;
        String maNguoiDung = SecurityUtils.getCurrentUser().getMaNguoiDung();
        if (trangThai != null) {
            responses = switch (trangThai) {
                case "CHO_THANH_TOAN" -> datVeService.findAllFilter(pageable, maNguoiDung, false, false)
                        .map(this::toDatVeResponse);
                case "DA_THANH_TOAN" -> datVeService.findAllFilter(pageable, maNguoiDung, true, null)
                        .map(this::toDatVeResponse);
                case "HET_HAN" -> datVeService.findAllFilter(pageable, maNguoiDung, false, true)
                        .map(this::toDatVeResponse);
                default -> throw new RuntimeException("Lỗi đường dẫn");
            };
        } else {
            responses = datVeService.findAllFilter(pageable, maNguoiDung, null, null)
                    .map(this::toDatVeResponse);
        }
        return ResponseEntity.ok(SuccessResponse.withData(responses));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        DatVe datVe = datVeService.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đặt vé này"));
        String maNguoiDung = SecurityUtils.getCurrentUser().getMaNguoiDung();
        if (SecurityUtils.hasRole("ADMIN") || maNguoiDung.equals(datVe.getKhachHang().getMaNguoiDung())) {
            return ResponseEntity.ok(SuccessResponse.withData(toDatVeResponse(datVe)));
        } else {
            return ResponseEntity.ok(ErrorResponse.withMessage("Không có quyền yêu cầu"));
        }
    }

    private DatVeResponse toDatVeResponse(DatVe datVe) {
        DatVeResponse response = new DatVeResponse();
        response.setMaDatVe(datVe.getMaDatVe());
        response.setTongTien(datVe.getTongTien());
        response.setTrangThai(datVe.getTrangThai());
        response.setThoiGianHetHan(datVe.getThoiGianHetHan());
        response.setHoatDong(datVe.getHoatDong());
        response.setUrl(datVe.getUrl());
        if (datVe.getVes() != null && !datVe.getVes().isEmpty()) {
            Optional<Ve> optional = datVe.getVes().stream().findFirst();
            SuKien suKien = optional.get().getLoaiVe().getSuKien();
            DatVeResponse.EventResponseInTicket dto = new DatVeResponse.EventResponseInTicket();
            BeanUtils.copyProperties(suKien, dto);

            if (suKien.getDiaDiem() != null) {
                EventResponse.DiaDiemResponse diaDiemDTO = new EventResponse.DiaDiemResponse();
                diaDiemDTO.setTenPhuongXa(suKien.getDiaDiem().getPhuongXa().getTenPhuongXa());
                diaDiemDTO.setTenQuanHuyen(suKien.getDiaDiem().getPhuongXa().getQuanHuyen().getTenQuanHuyen());
                diaDiemDTO.setTenTinhThanh(suKien.getDiaDiem().getPhuongXa().getQuanHuyen().getTinhThanh().getTenTinhThanh());
                diaDiemDTO.setMaDiaDiem(suKien.getDiaDiem().getMaDiaDiem());
                dto.setDiaDiem(diaDiemDTO);
            }

            if (suKien.getNguoiToChuc() != null) {
                DatVeResponse.NguoiDungResponse nguoiToChucDTO = new DatVeResponse.NguoiDungResponse();
                nguoiToChucDTO.setMaNguoiDung(suKien.getNguoiToChuc().getMaNguoiDung());
                nguoiToChucDTO.setTenHienThi(suKien.getNguoiToChuc().getTenHienThi());
                dto.setNguoiToChuc(nguoiToChucDTO);
            }

            response.setSuKien(dto);
        }

        List<DatVeResponse.ChiTietVeResponse> chiTietVes = veService.findByDatVe(datVe).stream()
                .map(ve -> {
                    DatVeResponse.ChiTietVeResponse chiTiet = new DatVeResponse.ChiTietVeResponse();
                    chiTiet.setMaVe(ve.getMaVe());
                    chiTiet.setTrangThai(ve.getTrangThai());
                    chiTiet.setThoiGianKiemVe(ve.getThoiGianKiemVe());

                    DatVeResponse.LoaiVeInfo loaiVeInfo = new DatVeResponse.LoaiVeInfo();
                    loaiVeInfo.setMaLoaiVe(ve.getLoaiVe().getMaLoaiVe());
                    loaiVeInfo.setTenLoaiVe(ve.getLoaiVe().getTenLoaiVe());
                    loaiVeInfo.setGiaTien(ve.getLoaiVe().getGiaTien());

                    chiTiet.setLoaiVe(loaiVeInfo);

                    return chiTiet;
                })
                .collect(Collectors.toList());

        response.setChiTietVes(chiTietVes);

        DatVeResponse.NguoiDungResponse khachHang = new DatVeResponse.NguoiDungResponse();
        khachHang.setMaNguoiDung(datVe.getKhachHang().getMaNguoiDung());
        khachHang.setTenHienThi(datVe.getKhachHang().getTenHienThi());
        khachHang.setEmail(datVe.getKhachHang().getEmail());

        response.setKhachHang(khachHang);

        return response;
    }
}
