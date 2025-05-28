package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.EventRequest;
import DuongVanBao.event.dto.request.KhuVucRequest;
import DuongVanBao.event.dto.request.LoaiVeRequest;
import DuongVanBao.event.dto.response.EventResponse;
import DuongVanBao.event.dto.response.KhuVucResponse;
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
import java.util.List;
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
                                           @RequestParam(required = false) String maDanhMuc,
                                           @RequestParam(required = false) Boolean hoatDong) {
        Page<EventResponse> responses = suKienService.findPageSuKien(maDanhMuc, hoatDong, pageable)
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
        List<KhuVuc> khuVucs = createKhuVucs(suKien, request.getKhuVucs());
        createLoaiVes(suKien, request.getLoaiVes(), khuVucs);

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

        if (!suKien.getDiaDiem().getTenDiaDiem().equals(request.getTenDiaDiem())
                || !suKien.getDiaDiem().getPhuongXa().getMaPhuongXa().equals(request.getMaPhuongXa())) {
            DiaDiem diaDiem = updateDiaDiem(request, suKien.getDiaDiem());
            suKien.setDiaDiem(diaDiem);
        }

        BeanUtils.copyProperties(request, suKien, "anhBia", "maDanhMucs", "loaiVes", "hoatDong");
        suKien.setHoatDong(suKien.isHoatDong());

        if (request.getAnhBia() != null && !request.getAnhBia().isEmpty()) {
            if (suKien.getAnhBia() != null) {
                fileUtil.deleteFile(suKien.getAnhBia());
            }
            String fileName = fileUtil.saveFile(request.getAnhBia());
            suKien.setAnhBia(fileName);
        }

        lienKetService.deleteBySuKienId(id);
        for (String maDanhMuc : request.getMaDanhMucs()) {
            DanhMucSuKien danhMuc = danhMucService.findById(maDanhMuc)
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

            LienKetSuKienDanhMuc lienKet = new LienKetSuKienDanhMuc();
            lienKet.setMaSuKien(suKien.getMaSuKien());
            lienKet.setMaDanhMuc(maDanhMuc);
            lienKet.setSuKien(suKien);
            lienKet.setDanhMuc(danhMuc);
            lienKetService.save(lienKet);
        }

        loaiVeService.deleteAllBySuKien(suKien);
        if (request.getLoaiVes() != null) {
            for (LoaiVeRequest loaiVeRequest : request.getLoaiVes()) {
                LoaiVe loaiVe = new LoaiVe();
                loaiVe.setSuKien(suKien);
                loaiVe.setTenLoaiVe(loaiVeRequest.getTenLoaiVe());
                loaiVe.setMoTa(loaiVeRequest.getMoTa());
                loaiVe.setSoLuong(loaiVeRequest.getSoLuong());
                loaiVe.setSoLuongToiThieu(loaiVeRequest.getSoLuongToiThieu());
                loaiVe.setSoLuongToiDa(loaiVeRequest.getSoLuongToiDa());
                loaiVe.setGiaTien(loaiVeRequest.getGiaTien());
                KhuVuc kv = khuVucRepository.findById("1").get();
                loaiVe.setKhuVuc(kv);
                loaiVeService.save(loaiVe);
            }
        }

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

    private void validateDanhMucs(String[] maDanhMucs) {
        for (String maDanhMuc : maDanhMucs) {
            if (!danhMucService.existsById(maDanhMuc)) {
                throw new RuntimeException("Danh mục với mã " + maDanhMuc + " không tồn tại");
            }
        }
    }

    private List<KhuVuc> createKhuVucs(SuKien suKien, List<KhuVucRequest> khuVucRequests) {
        if (khuVucRequests == null || khuVucRequests.isEmpty()) {
            KhuVuc defaultKhuVuc = new KhuVuc();
            defaultKhuVuc.setTenKhuVuc("Khu vực mặc định");
            defaultKhuVuc.setViTri("Mặc định");
            defaultKhuVuc.setMoTa("Khu vực mặc định cho sự kiện");
            defaultKhuVuc.setLayoutData("{}");
            defaultKhuVuc.setSuKien(suKien);
            return List.of(khuVucRepository.save(defaultKhuVuc));
        }

        return khuVucRequests.stream()
                .map(request -> {
                    KhuVuc khuVuc = new KhuVuc();
                    khuVuc.setTenKhuVuc(request.getTenKhuVuc());
                    khuVuc.setMoTa(request.getMoTa());
                    khuVuc.setViTri(request.getViTri());
                    khuVuc.setLayoutData(request.getLayoutData());
                    khuVuc.setSuKien(suKien);
                    khuVuc.setMaKhuVuc(request.getTempId());
                    return khuVucRepository.save(khuVuc);
                })
                .collect(Collectors.toList());
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

    private DiaDiem updateDiaDiem(EventRequest request, DiaDiem diaDiemUpdate) {
        diaDiemUpdate.setTenDiaDiem(request.getTenDiaDiem());
        diaDiemUpdate.setPhuongXa(phuongXaService.findById(request.getMaPhuongXa())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phường xã")));
        return diaDiemService.save(diaDiemUpdate);
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

    private void createLoaiVes(SuKien suKien, List<LoaiVeRequest> loaiVes, List<KhuVuc> khuVucs) {
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

                KhuVuc khuVuc = khuVucs.stream()
                        .filter(zone -> zone.getMaKhuVuc().equals(request.getMaKhuVuc()) ||
                                (zone.getLayoutData() != null &&
                                        zone.getLayoutData().contains(request.getMaKhuVuc())))
                        .findFirst()
                        .orElse(khuVucs.isEmpty() ? null : khuVucs.get(0));

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

        List<EventResponse.LoaiVeResponse> loaiVeList = loaiVeService.findBySuKien(suKien)
            .stream()
            .map(loaiVe -> {
                EventResponse.LoaiVeResponse loaiVeResponse = new EventResponse.LoaiVeResponse();
                BeanUtils.copyProperties(loaiVe, loaiVeResponse);
                return loaiVeResponse;
            })
            .collect(Collectors.toList());
        response.setLoaiVes(loaiVeList);

        List<KhuVucResponse> khuVucList = khuVucRepository.findAllBySuKien(suKien)
                .stream()
                .map(khuVuc -> {
                    KhuVucResponse khuVucResponse = new KhuVucResponse();
                    BeanUtils.copyProperties(khuVuc, khuVucResponse);
                    khuVucResponse.setTempId(khuVuc.getMaKhuVuc());
                    return khuVucResponse;
                })
                .collect(Collectors.toList());
        response.setKhuVucs(khuVucList);
        return response;
    }
}