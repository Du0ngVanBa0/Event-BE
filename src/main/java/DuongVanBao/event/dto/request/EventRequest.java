package DuongVanBao.event.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequest {
    private String tieuDe;

    private String moTa;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime thoiGianBatDau;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime thoiGianKetThuc;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ngayMoBanVe;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ngayDongBanVe;

    private MultipartFile anhBia;

    private String tenDiaDiem;

    private String maPhuongXa;

    private String[] maDanhMucs;

    private List<LoaiVeRequest> loaiVes;
    @Getter
    private List<KhuVucRequest> khuVucs;

    public void setLoaiVes(String loaiVesJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.loaiVes = mapper.readValue(loaiVesJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, LoaiVeRequest.class));
        } catch (Exception e) {
            throw new RuntimeException("Invalid loaiVes format");
        }
    }

    public void setKhuVucs(String khuVucsJson) {
        try {
            if (khuVucsJson == null || khuVucsJson.trim().isEmpty()) {
                this.khuVucs = List.of();
                return;
            }

            String trimmed = khuVucsJson.trim();

            if ("object".equals(trimmed) ||
                    trimmed.contains("[object Object]") ||
                    trimmed.matches("\\[object Object\\](,\\[object Object\\])*")) {
                System.err.println("Received invalid object string format: " + trimmed);
                this.khuVucs = List.of();
                return;
            }

            if ("[]".equals(trimmed)) {
                this.khuVucs = List.of();
                return;
            }

            System.out.println("KhuVucs JSON input: " + khuVucsJson);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            if (trimmed.startsWith("[")) {
                this.khuVucs = mapper.readValue(trimmed,
                        mapper.getTypeFactory().constructCollectionType(List.class, KhuVucRequest.class));
            } else if (trimmed.startsWith("{")) {
                KhuVucRequest singleKhuVuc = mapper.readValue(trimmed, KhuVucRequest.class);
                this.khuVucs = List.of(singleKhuVuc);
            } else {
                System.err.println("Invalid khuVucs format - not JSON: " + khuVucsJson);
                this.khuVucs = List.of();
            }

            System.out.println("Successfully parsed " + this.khuVucs.size() + " khu vucs");
            this.khuVucs.forEach(kv -> {
                System.out.println("  - Template: " + kv.getMaTemplate() +
                        ", Custom name: " + kv.getTenTuyChon() +
                        ", Position: (" + kv.getToaDoX() + ", " + kv.getToaDoY() + ")");
            });

        } catch (Exception e) {
            System.err.println("Error processing khuVucs: " + e.getMessage());
            System.err.println("Input was: " + khuVucsJson);
            e.printStackTrace();
            this.khuVucs = List.of();
        }
    }
}