package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.DatVeRequest;
import DuongVanBao.event.dto.response.*;
import DuongVanBao.event.dto.search.DatVeSearchAdmin;
import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.LoaiVe;
import DuongVanBao.event.model.entity.SuKien;
import DuongVanBao.event.model.entity.Ve;
import DuongVanBao.event.service.DatVeService;
import DuongVanBao.event.service.LoaiVeService;
import DuongVanBao.event.service.VeService;
import DuongVanBao.event.service.impl.ThanhToanService;
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
    private final ThanhToanService thanhToanService;

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
                    now.isBefore(loaiVe.getSuKien().getNgayMoBanVe())) {
                throw new RuntimeException("Chưa đến thời gian bán vé");
            }

            if (loaiVe.getSuKien().getNgayDongBanVe() != null &&
                    now.isAfter(loaiVe.getSuKien().getNgayDongBanVe())) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<?> findAll(Pageable pageable, @RequestBody(required = false) DatVeSearchAdmin request) {
        if (request.getTrangThai() != null) {
            switch(request.getTrangThai()) {
                case "CHO_THANH_TOAN":
                    request.setHoatDong(false);
                    request.setIsExpired(false);
                    break;
                case "DA_THANH_TOAN":
                    request.setHoatDong(true);
                    break;
                case "HET_HAN":
                    request.setHoatDong(false);
                    request.setIsExpired(true);
                    break;
                default:
                    throw new RuntimeException("Lỗi đường dẫn");
            };
        }
        Page<DatVeResponse> responses = datVeService.findPageFilterAdmin(pageable, request)
                .map(this::toDatVeResponse);
        return ResponseEntity.ok(SuccessResponse.withData(responses));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        DatVe datVe = datVeService.findById(id).orElseThrow(() ->
                new RuntimeException("Không tìm thấy đặt vé này"));

        veService.deleteByDatVe(datVe);
        thanhToanService.deleteByDatVe(datVe);
        datVeService.deleteById(id);
        return ResponseEntity.ok(SuccessResponse.withMessage(null, "Xóa thành công"));
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/check-in/{id}")
    public ResponseEntity<?> checkIn(@PathVariable String id) {
        Ve ve = veService.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));
        String maNguoiDung = SecurityUtils.getCurrentUser().getMaNguoiDung();
        if (!SecurityUtils.hasRole("ADMIN") || !maNguoiDung.equals(ve.getLoaiVe().getSuKien().getNguoiToChuc().getMaNguoiDung())) {
            return ResponseEntity.ok(SuccessResponse.withData(toVeResponse(ve)));
        } else {
            return ResponseEntity.ok(ErrorResponse.withMessage("Không tìm thấy vé"));
        }

    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/use/{id}")
    public ResponseEntity<?> useTicket(@PathVariable String id) {
        try {
            Ve ve = veService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));

            String maNguoiDung = SecurityUtils.getCurrentUser().getMaNguoiDung();

            boolean isAdmin = SecurityUtils.hasRole("ADMIN");
            boolean isOrganizer = maNguoiDung.equals(
                    ve.getLoaiVe().getSuKien().getNguoiToChuc().getMaNguoiDung()
            );

            if (!isAdmin && !isOrganizer) {
                return ResponseEntity.ok(ErrorResponse.withMessage("Không tìm thấy vé"));
            }

            if ("DA_SU_DUNG".equals(ve.getTrangThai())) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.withMessage("Vé đã được sử dụng"));
            }

            if (!ve.getDatVe().getHoatDong()) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.withMessage("Đặt vé chưa được kích hoạt hoặc đã hết hạn"));
            }

            ve.setTrangThai("DA_SU_DUNG");
            ve.setThoiGianKiemVe(LocalDateTime.now());
            Ve updatedVe = veService.save(ve);

            return ResponseEntity.ok(SuccessResponse.withData(toVeResponse(updatedVe)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.withMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ErrorResponse.withMessage("Lỗi hệ thống"));
        }
    }

    private VeResponse toVeResponse(Ve ve) {
        VeResponse response = new VeResponse();

        response.setMaVe(ve.getMaVe());
        response.setTrangThai(ve.getTrangThai());
        response.setThoiGianKiemVe(ve.getThoiGianKiemVe());

        // Set ticket type info
        if (ve.getLoaiVe() != null) {
            VeResponse.LoaiVeInfo loaiVeInfo = new VeResponse.LoaiVeInfo();
            loaiVeInfo.setMaLoaiVe(ve.getLoaiVe().getMaLoaiVe());
            loaiVeInfo.setTenLoaiVe(ve.getLoaiVe().getTenLoaiVe());
            loaiVeInfo.setMoTa(ve.getLoaiVe().getMoTa());
            loaiVeInfo.setGiaTien(ve.getLoaiVe().getGiaTien());

            // Set area info
            if (ve.getLoaiVe().getKhuVuc() != null) {
                VeResponse.KhuVucInfo khuVucInfo = new VeResponse.KhuVucInfo();
                khuVucInfo.setMaKhuVuc(ve.getLoaiVe().getKhuVuc().getMaKhuVuc());
                khuVucInfo.setTenKhuVuc(ve.getLoaiVe().getKhuVuc().getTenHienThi());
                loaiVeInfo.setKhuVuc(khuVucInfo);
            }

            response.setLoaiVe(loaiVeInfo);

            // Set event info
            if (ve.getLoaiVe().getSuKien() != null) {
                VeResponse.EventInfo eventInfo = new VeResponse.EventInfo();
                SuKien suKien = ve.getLoaiVe().getSuKien();

                eventInfo.setMaSuKien(suKien.getMaSuKien());
                eventInfo.setTieuDe(suKien.getTieuDe());
                eventInfo.setAnhBia(suKien.getAnhBia());
                eventInfo.setThoiGianBatDau(suKien.getThoiGianBatDau());
                eventInfo.setThoiGianKetThuc(suKien.getThoiGianKetThuc());
                eventInfo.setHoatDong(suKien.isHoatDong());

                // Set venue info
                if (suKien.getDiaDiem() != null) {
                    VeResponse.DiaDiemInfo diaDiemInfo = new VeResponse.DiaDiemInfo();
                    diaDiemInfo.setMaDiaDiem(suKien.getDiaDiem().getMaDiaDiem());
                    diaDiemInfo.setTenPhuongXa(suKien.getDiaDiem().getPhuongXa().getTenPhuongXa());
                    diaDiemInfo.setTenQuanHuyen(suKien.getDiaDiem().getPhuongXa().getQuanHuyen().getTenQuanHuyen());
                    diaDiemInfo.setTenTinhThanh(suKien.getDiaDiem().getPhuongXa().getQuanHuyen().getTinhThanh().getTenTinhThanh());
                    eventInfo.setDiaDiem(diaDiemInfo);
                }

                // Set organizer info
                if (suKien.getNguoiToChuc() != null) {
                    VeResponse.NguoiToChucInfo nguoiToChucInfo = new VeResponse.NguoiToChucInfo();
                    nguoiToChucInfo.setMaNguoiDung(suKien.getNguoiToChuc().getMaNguoiDung());
                    nguoiToChucInfo.setTenHienThi(suKien.getNguoiToChuc().getTenHienThi());
                    eventInfo.setNguoiToChuc(nguoiToChucInfo);
                }

                response.setSuKien(eventInfo);
            }
        }

        // Set booking info
        if (ve.getDatVe() != null) {
            VeResponse.DatVeInfo datVeInfo = new VeResponse.DatVeInfo();
            datVeInfo.setMaDatVe(ve.getDatVe().getMaDatVe());
            datVeInfo.setTrangThai(ve.getDatVe().getTrangThai());
            datVeInfo.setTongTien(ve.getDatVe().getTongTien());
            datVeInfo.setThoiGianHetHan(ve.getDatVe().getThoiGianHetHan());
            datVeInfo.setHoatDong(ve.getDatVe().getHoatDong());
            response.setDatVe(datVeInfo);

            if (ve.getDatVe().getKhachHang() != null) {
                VeResponse.KhachHangInfo khachHangInfo = new VeResponse.KhachHangInfo();
                khachHangInfo.setMaNguoiDung(ve.getDatVe().getKhachHang().getMaNguoiDung());
                khachHangInfo.setTenHienThi(ve.getDatVe().getKhachHang().getTenHienThi());
                khachHangInfo.setEmail(ve.getDatVe().getKhachHang().getEmail());
                response.setKhachHang(khachHangInfo);
            }
        }

        return response;
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
