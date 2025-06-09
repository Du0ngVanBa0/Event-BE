package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.EventRequest;
import DuongVanBao.event.dto.request.KhuVucRequest;
import DuongVanBao.event.dto.request.LoaiVeRequest;
import DuongVanBao.event.dto.response.EventResponse;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.model.entity.*;
import DuongVanBao.event.repository.KhuVucRepository;
import DuongVanBao.event.service.*;
import DuongVanBao.event.util.FileUtil;
import DuongVanBao.event.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static DuongVanBao.event.util.SecurityUtils.getCurrentUserId;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class SuKienController implements BaseController<EventRequest, String> {
    private final SuKienService suKienService;
    private final DiaDiemService diaDiemService;
    private final PhuongXaService phuongXaService;
    private final KhuVucMauService khuVucMauService;
    private final VeService veService;
    private final LienKetSuKienDanhMucService lienKetService;
    private final DanhMucSuKienService danhMucService;
    private final LoaiVeService loaiVeService;
    private final KhuVucRepository khuVucRepository;
    private final FileUtil fileUtil;

    @Override
    public ResponseEntity<?> getAll() {
        List<EventResponse> responses = suKienService.findAll().stream()
            .map(this::toEventResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(SuccessResponse.withData(responses));
    }

    @Override
    public ResponseEntity<?> getPage(Pageable pageable) {
        Page<EventResponse> responses = suKienService.findAll(pageable)
                .map(this::toEventResponse);
        return ResponseEntity.ok(SuccessResponse.withData(responses));
    }

    @GetMapping("/page-filter")
    public ResponseEntity<?> getPageFilter(Pageable pageable,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String maDanhMuc,
                                           @RequestParam(required = false) Boolean hoatDong) {
        Page<EventResponse> responses = suKienService.findPageSuKien(name, maDanhMuc, hoatDong, pageable)
                .map(this::toEventResponse);
        return ResponseEntity.ok(SuccessResponse.withData(responses));
    }

    @Override
    public ResponseEntity<?> getById(String id) {
        SuKien suKien = suKienService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        EventResponse eventResponse = toEventResponse(suKien);

        List<EventResponse.LoaiVeResponse> loaiVeList = eventResponse.getLoaiVes();
        if (loaiVeList != null) {
            loaiVeList.forEach(loaiVe -> {
                Integer totalTickets = loaiVe.getSoLuong();
                Integer reservedTickets = veService.calculateReservedTickets(loaiVe.getMaLoaiVe());
                Integer remainingTickets = totalTickets - reservedTickets;

                loaiVe.setVeConLai(remainingTickets);
            });
        }

        return ResponseEntity.ok(SuccessResponse.withData(eventResponse));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Override
    @Transactional
    public ResponseEntity<?> create(@ModelAttribute EventRequest request) {
        validateDanhMucs(request.getMaDanhMucs());
        
        SuKien suKien = createSuKien(request);
        createDanhMucLinks(suKien, request.getMaDanhMucs());
        Map<String, KhuVuc> templateToKhuVucMap = createKhuVucs(suKien, request.getKhuVucs());
        createLoaiVes(suKien, request.getLoaiVes(), templateToKhuVucMap);

        return ResponseEntity.ok(SuccessResponse.withData(toEventResponse(suKien)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Transactional
    @Override
    public ResponseEntity<?> update(String id,@ModelAttribute EventRequest request) {
        if (!danhMucService.existsByIds(Arrays.asList(request.getMaDanhMucs()))) {
            throw new RuntimeException("Một hoặc nhiều danh mục không tồn tại");
        }

        SuKien suKien = suKienService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        if (!SecurityUtils.hasRole("ADMIN")) {
            if (!suKien.getNguoiToChuc().getMaNguoiDung().equals(getCurrentUserId())) {
                throw new AccessDeniedException("Bạn không có quyền truy cập sự kiện này");
            }
        }

        DiaDiem diaDiem = suKien.getDiaDiem();
        if (diaDiem != null) {
            diaDiem.setTenDiaDiem(request.getTenDiaDiem());
            if (!diaDiem.getPhuongXa().getMaPhuongXa().equals(request.getMaPhuongXa())) {
                diaDiem.setPhuongXa(phuongXaService.findById(request.getMaPhuongXa())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phường xã")));
            }
            diaDiemService.save(diaDiem);
        }

        BeanUtils.copyProperties(request, suKien, "anhBia", "maDanhMucs", "khuVucs", "loaiVes", "hoatDong");

        if (request.getAnhBia() != null && !request.getAnhBia().isEmpty()) {
            if (suKien.getAnhBia() != null) {
                fileUtil.deleteFile(suKien.getAnhBia());
            }
            String fileName = fileUtil.saveFile(request.getAnhBia());
            suKien.setAnhBia(fileName);
        }

        suKienService.updateDanhMucLinks(suKien, request.getMaDanhMucs());
        Map<String, KhuVuc> updatedKhuVucs = updateKhuVucs(suKien, request.getKhuVucs());
        updateLoaiVes(suKien, request.getLoaiVes(), updatedKhuVucs);

        suKien = suKienService.save(suKien);
        return ResponseEntity.ok(SuccessResponse.withData(toEventResponse(suKien)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Override
    @Transactional
    public ResponseEntity<?> delete(String id) {
        SuKien suKien = suKienService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        if (!SecurityUtils.hasRole("ADMIN")) {
            if (!suKien.getNguoiToChuc().getMaNguoiDung().equals(getCurrentUserId())) {
                throw new AccessDeniedException("Bạn không có quyền xem sự kiện này");
            }
        }

        lienKetService.deleteBySuKienId(suKien.getMaSuKien());
        loaiVeService.deleteAllBySuKien(suKien);
        khuVucRepository.deleteAllBySuKien(suKien);
        suKienService.deleteById(id);
        diaDiemService.deleteById(suKien.getDiaDiem().getMaDiaDiem());

        return ResponseEntity.ok(SuccessResponse.withMessage(null, "Xóa thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable String id) {
        SuKien suKien = suKienService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        suKien.setHoatDong(true);
        suKien = suKienService.save(suKien);

        return ResponseEntity.ok(SuccessResponse.withData(toEventResponse(suKien)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unapproved")
    public ResponseEntity<?> getUnapprovedEvents(Pageable pageable) {
        Page<EventResponse> responses = suKienService.findAllByHoatDong(false, pageable)
                .map(this::toEventResponse);
        return ResponseEntity.ok(SuccessResponse.withData(responses));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/mine")
    public ResponseEntity<?> getMyEvents(
            @RequestParam(required = false) Boolean approved,
            Pageable pageable
    ) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        Page<EventResponse> responses;

        if (approved != null) {
            responses = suKienService.findByNguoiToChucAndHoatDong(currentUserId, approved, pageable)
                    .map(this::toEventResponse);
        } else {
            responses = suKienService.findByNguoiToChuc(currentUserId, pageable)
                    .map(this::toEventResponse);
        }

        return ResponseEntity.ok(SuccessResponse.withData(responses));
    }

    private Map<String, KhuVuc> updateKhuVucs(SuKien suKien, List<KhuVucRequest> khuVucRequests) {
        List<KhuVuc> existingKhuVucs = khuVucRepository.findAllBySuKien(suKien);
        Map<String, KhuVuc> templateToKhuVucMap = new HashMap<>();

        if (khuVucRequests == null || khuVucRequests.isEmpty()) {
            return templateToKhuVucMap;
        }

        Map<String, KhuVuc> existingKhuVucMap = existingKhuVucs.stream()
                .collect(Collectors.toMap(KhuVuc::getMaKhuVuc, khuVuc -> khuVuc));

        for (KhuVucRequest request : khuVucRequests) {
            KhuVucMau template = khuVucMauService.findById(request.getMaKhuVucMau())
                    .orElseThrow(() -> new RuntimeException("Khu vực mẫu không tồn tại: " + request.getMaKhuVucMau()));

            KhuVuc existingKhuVuc = existingKhuVucs.stream()
                    .filter(kv -> kv.getTemplate() != null &&
                            kv.getTemplate().getMaKhuVucMau().equals(request.getMaKhuVucMau()))
                    .findFirst()
                    .orElse(null);

            if (existingKhuVuc != null) {
                existingKhuVuc.setTenTuyChon(request.getTenTuyChon());
                existingKhuVuc.setMoTaTuyChon(request.getMoTaTuyChon());
                existingKhuVuc.setMauSacTuyChon(request.getMauSacTuyChon());
                existingKhuVuc.setToaDoX(request.getToaDoX() != null ? request.getToaDoX() : template.getToaDoXMacDinh());
                existingKhuVuc.setToaDoY(request.getToaDoY() != null ? request.getToaDoY() : template.getToaDoYMacDinh());
                existingKhuVuc.setChieuRong(request.getChieuRong() != null ? request.getChieuRong() : template.getChieuRongMacDinh());
                existingKhuVuc.setChieuCao(request.getChieuCao() != null ? request.getChieuCao() : template.getChieuCaoMacDinh());
                existingKhuVuc.setViTri(request.getViTri() != null ? request.getViTri() :
                        String.format("Vị trí (%d, %d)", existingKhuVuc.getToaDoX(), existingKhuVuc.getToaDoY()));

                KhuVuc savedKhuVuc = khuVucRepository.save(existingKhuVuc);
                templateToKhuVucMap.put(request.getMaKhuVucMau(), savedKhuVuc);

                existingKhuVucMap.remove(existingKhuVuc.getMaKhuVuc());
            } else {
                KhuVuc newKhuVuc = new KhuVuc();
                newKhuVuc.setTemplate(template);
                newKhuVuc.setSuKien(suKien);
                newKhuVuc.setTenTuyChon(request.getTenTuyChon());
                newKhuVuc.setMoTaTuyChon(request.getMoTaTuyChon());
                newKhuVuc.setMauSacTuyChon(request.getMauSacTuyChon());
                newKhuVuc.setToaDoX(request.getToaDoX() != null ? request.getToaDoX() : template.getToaDoXMacDinh());
                newKhuVuc.setToaDoY(request.getToaDoY() != null ? request.getToaDoY() : template.getToaDoYMacDinh());
                newKhuVuc.setChieuRong(request.getChieuRong() != null ? request.getChieuRong() : template.getChieuRongMacDinh());
                newKhuVuc.setChieuCao(request.getChieuCao() != null ? request.getChieuCao() : template.getChieuCaoMacDinh());
                newKhuVuc.setViTri(request.getViTri() != null ? request.getViTri() :
                        String.format("Vị trí (%d, %d)", newKhuVuc.getToaDoX(), newKhuVuc.getToaDoY()));
                newKhuVuc.setHoatDong(true);

                KhuVuc savedKhuVuc = khuVucRepository.save(newKhuVuc);
                templateToKhuVucMap.put(request.getMaKhuVucMau(), savedKhuVuc);
            }
        }

        return templateToKhuVucMap;
    }

    private void updateLoaiVes(SuKien suKien, List<LoaiVeRequest> loaiVeRequests, Map<String, KhuVuc> khuVucMap) {
        loaiVeService.deleteAllBySuKien(suKien);

        if (loaiVeRequests != null && !loaiVeRequests.isEmpty()) {
            for (LoaiVeRequest request : loaiVeRequests) {
                LoaiVe loaiVe = new LoaiVe();
                loaiVe.setSuKien(suKien);
                loaiVe.setTenLoaiVe(request.getTenLoaiVe());
                loaiVe.setMoTa(request.getMoTa());
                loaiVe.setSoLuong(request.getSoLuong());
                loaiVe.setSoLuongToiThieu(request.getSoLuongToiThieu());
                loaiVe.setSoLuongToiDa(request.getSoLuongToiDa());
                loaiVe.setGiaTien(request.getGiaTien());

                KhuVuc khuVuc = khuVucMap.get(request.getMaKhuVuc());
                if (khuVuc == null && !khuVucMap.isEmpty()) {
                    khuVuc = khuVucMap.values().iterator().next();
                }

                if (khuVuc != null) {
                    loaiVe.setKhuVuc(khuVuc);
                }

                loaiVeService.save(loaiVe);
            }
        }
    }

    private void validateDanhMucs(String[] maDanhMucs) {
        for (String maDanhMuc : maDanhMucs) {
            if (!danhMucService.existsById(maDanhMuc)) {
                throw new RuntimeException("Danh mục với mã " + maDanhMuc + " không tồn tại");
            }
        }
    }

    private Map<String,KhuVuc> createKhuVucs(SuKien suKien, List<KhuVucRequest> khuVucRequests) {
        Map<String, KhuVuc> templateToKhuVucMap = new HashMap<>();

        if (khuVucRequests == null || khuVucRequests.isEmpty()) {
            // Create default zone if no zones specified
            KhuVuc defaultKhuVuc = new KhuVuc();
            // Cần tạo KhuVucMau default hoặc handle case này
            defaultKhuVuc.setTenTuyChon("Khu vực mặc định");
            defaultKhuVuc.setViTri("Vị trí (0, 0)");
            defaultKhuVuc.setMoTaTuyChon("Khu vực mặc định cho sự kiện");
            defaultKhuVuc.setSuKien(suKien);
            defaultKhuVuc.setHoatDong(true);

            // Tìm template mặc định hoặc tạo một cái
            KhuVucMau defaultTemplate = khuVucMauService.findByHoatDongTrueOrderByThuTuHienThi().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Không có template khu vực nào available"));
            defaultKhuVuc.setTemplate(defaultTemplate);

            KhuVuc savedKhuVuc = khuVucRepository.save(defaultKhuVuc);
            templateToKhuVucMap.put("default", savedKhuVuc);
            return templateToKhuVucMap;
        }

        for (KhuVucRequest request : khuVucRequests) {
            KhuVucMau template = khuVucMauService.findById(request.getMaKhuVucMau())
                    .orElseThrow(() -> new RuntimeException("Template không tồn tại: " + request.getMaKhuVucMau()));

            KhuVuc khuVuc = new KhuVuc();
            khuVuc.setTemplate(template);
            khuVuc.setSuKien(suKien);

            khuVuc.setTenTuyChon(request.getTenTuyChon());
            khuVuc.setMoTaTuyChon(request.getMoTaTuyChon());
            khuVuc.setMauSacTuyChon(request.getMauSacTuyChon());

            khuVuc.setToaDoX(request.getToaDoX() != null ? request.getToaDoX() : template.getToaDoXMacDinh());
            khuVuc.setToaDoY(request.getToaDoY() != null ? request.getToaDoY() : template.getToaDoYMacDinh());
            khuVuc.setChieuRong(request.getChieuRong() != null ? request.getChieuRong() : template.getChieuRongMacDinh());
            khuVuc.setChieuCao(request.getChieuCao() != null ? request.getChieuCao() : template.getChieuCaoMacDinh());

            khuVuc.setViTri(request.getViTri() != null ? request.getViTri() :
                    String.format("Vị trí (%d, %d)", khuVuc.getToaDoX(), khuVuc.getToaDoY()));

            khuVuc.setHoatDong(true);

            KhuVuc savedKhuVuc = khuVucRepository.save(khuVuc);
            templateToKhuVucMap.put(request.getMaKhuVucMau(), savedKhuVuc);
        }

        return templateToKhuVucMap;
    }

    private SuKien createSuKien(EventRequest request) {
        DiaDiem diaDiem = createDiaDiem(request);
        
        SuKien suKien = new SuKien();
        BeanUtils.copyProperties(request, suKien, "anhBia", "maDanhMucs", "loaiVes", "hoatDong");
        
        if (request.getAnhBia() != null && !request.getAnhBia().isEmpty()) {
            String fileName = fileUtil.saveFile(request.getAnhBia());
            suKien.setAnhBia(fileName);
        }
        
        suKien.setDiaDiem(diaDiem);
        suKien.setNguoiToChuc(SecurityUtils.getCurrentUser());
        suKien.setHoatDong(false);
        suKien.setNgayTao(LocalDateTime.now());
        
        return suKienService.save(suKien);
    }

    private DiaDiem createDiaDiem(EventRequest request) {
        DiaDiem diaDiem = new DiaDiem();
        diaDiem.setTenDiaDiem(request.getTenDiaDiem());
        diaDiem.setPhuongXa(phuongXaService.findById(request.getMaPhuongXa())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy phường xã")));
        return diaDiemService.save(diaDiem);
    }

    private void createDanhMucLinks(SuKien suKien, String[] maDanhMucs) {
        Arrays.stream(maDanhMucs).forEach(maDanhMuc -> {
            DanhMucSuKien danhMuc = danhMucService.findById(maDanhMuc)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
                
            LienKetSuKienDanhMuc lienKet = new LienKetSuKienDanhMuc();
            lienKet.setMaSuKien(suKien.getMaSuKien());
            lienKet.setMaDanhMuc(maDanhMuc);
            lienKet.setSuKien(suKien);
            lienKet.setDanhMuc(danhMuc);
            lienKetService.save(lienKet);
        });
    }

    private void createLoaiVes(SuKien suKien, List<LoaiVeRequest> loaiVes, Map<String, KhuVuc> templateToKhuVucMap) {
        if (loaiVes != null) {
            loaiVes.forEach(request -> {
                LoaiVe loaiVe = new LoaiVe();
                loaiVe.setSuKien(suKien);
                loaiVe.setTenLoaiVe(request.getTenLoaiVe());
                loaiVe.setMoTa(request.getMoTa());
                loaiVe.setSoLuong(request.getSoLuong());
                loaiVe.setSoLuongToiThieu(request.getSoLuongToiThieu());
                loaiVe.setSoLuongToiDa(request.getSoLuongToiDa());
                loaiVe.setGiaTien(request.getGiaTien());

                KhuVuc khuVuc = templateToKhuVucMap.get(request.getMaKhuVuc());
                if (khuVuc == null) {
                    khuVuc = templateToKhuVucMap.values().iterator().next();
                    System.err.println("Warning: Could not find zone for template " + request.getMaKhuVuc() +
                            ", using fallback zone: " + khuVuc.getMaKhuVuc());
                }

                loaiVe.setKhuVuc(khuVuc);
                loaiVeService.save(loaiVe);
            });
        }
    }

    private EventResponse toEventResponse(SuKien suKien) {
        EventResponse response = new EventResponse();
        BeanUtils.copyProperties(suKien, response);

        EventResponse.DiaDiemResponse diaDiemResponse = new EventResponse.DiaDiemResponse();
        DiaDiem diaDiem = suKien.getDiaDiem();
        diaDiemResponse.setMaDiaDiem(diaDiem.getMaDiaDiem());
        diaDiemResponse.setTenDiaDiem(diaDiem.getTenDiaDiem());

        diaDiemResponse.setMaPhuongXa(diaDiem.getPhuongXa().getMaPhuongXa());
        diaDiemResponse.setTenPhuongXa(diaDiem.getPhuongXa().getTenPhuongXa());

        diaDiemResponse.setMaQuanHuyen(diaDiem.getPhuongXa().getQuanHuyen().getMaQuanHuyen());
        diaDiemResponse.setTenQuanHuyen(diaDiem.getPhuongXa().getQuanHuyen().getTenQuanHuyen());

        diaDiemResponse.setMaTinhThanh(diaDiem.getPhuongXa().getQuanHuyen().getTinhThanh().getMaTinhThanh());
        diaDiemResponse.setTenTinhThanh(diaDiem.getPhuongXa().getQuanHuyen().getTinhThanh().getTenTinhThanh());
        response.setDiaDiem(diaDiemResponse);

        List<EventResponse.DanhMucResponse> danhMucList = lienKetService.findBySuKienId(suKien.getMaSuKien())
            .stream()
            .map(lienKet -> {
                EventResponse.DanhMucResponse danhMucResponse = new EventResponse.DanhMucResponse();
                danhMucResponse.setMaDanhMuc(lienKet.getMaDanhMuc());
                danhMucResponse.setTenDanhMuc(lienKet.getDanhMuc().getTenDanhMuc());
                return danhMucResponse;
            })
            .collect(Collectors.toList());
        response.setDanhMucs(danhMucList);

        List<EventResponse.KhuVucResponse> khuVucList = khuVucRepository.findAllBySuKien(suKien)
                .stream()
                .filter(KhuVuc::isHoatDong)
                .map(this::toKhuVucResponse)
                .collect(Collectors.toList());
        response.setKhuVucs(khuVucList);

        List<EventResponse.LoaiVeResponse> loaiVeList = loaiVeService.findBySuKien(suKien)
            .stream()
            .map(loaiVe -> {
                EventResponse.LoaiVeResponse loaiVeResponse = new EventResponse.LoaiVeResponse();
                BeanUtils.copyProperties(loaiVe, loaiVeResponse);

                if (loaiVe.getKhuVuc() != null) {
                    loaiVeResponse.setMaKhuVuc(loaiVe.getKhuVuc().getMaKhuVuc());
                }
                return loaiVeResponse;
            })
            .collect(Collectors.toList());
        response.setLoaiVes(loaiVeList);

        return response;
    }

    private EventResponse.KhuVucResponse toKhuVucResponse(KhuVuc khuVuc) {
        EventResponse.KhuVucResponse response = new EventResponse.KhuVucResponse();

        response.setMaKhuVuc(khuVuc.getMaKhuVuc());
        response.setTenHienThi(khuVuc.getTenHienThi());
        response.setMoTa(khuVuc.getMoTaTuyChon());
        response.setViTri(khuVuc.getViTri());
        response.setMauSacHienThi(khuVuc.getMauSacHienThi());
        response.setToaDoX(khuVuc.getToaDoX());
        response.setToaDoY(khuVuc.getToaDoY());
        response.setChieuRong(khuVuc.getChieuRong());
        response.setChieuCao(khuVuc.getChieuCao());
        response.setHoatDong(khuVuc.isHoatDong());

        if (khuVuc.getTemplate() != null) {
            KhuVucMau template = khuVuc.getTemplate();
            response.setTenGoc(template.getTenKhuVuc());

            EventResponse.KhuVucResponse.KhuVucMauInfo templateInfo =
                    new EventResponse.KhuVucResponse.KhuVucMauInfo();
            templateInfo.setMaKhuVucMau(template.getMaKhuVucMau());
            templateInfo.setTenKhuVuc(template.getTenKhuVuc());
            templateInfo.setMauSac(template.getMauSac());
            templateInfo.setHinhDang(template.getHinhDang());
            templateInfo.setThuTuHienThi(template.getThuTuHienThi());

            response.setTemplate(templateInfo);
        } else {
            response.setTenGoc("Khu vực tùy chỉnh");
        }

        return response;
    }
}