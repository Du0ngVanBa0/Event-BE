package DuongVanBao.event.controller;

import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.model.entity.TinhThanh;
import DuongVanBao.event.service.TinhThanhService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {
    private final TinhThanhService tinhThanhService;

    @GetMapping
    public ResponseEntity<?> getAllPlaces() {
        List<TinhThanh> tinhThanhList = tinhThanhService.findAll();
        
        return ResponseEntity.ok(SuccessResponse.withData(tinhThanhList));
    }
}
