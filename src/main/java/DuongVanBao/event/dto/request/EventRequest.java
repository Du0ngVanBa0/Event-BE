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

public class EventRequest {
    @Setter
    @Getter
    private String tieuDe;
    @Setter
    @Getter
    private String moTa;

    @Setter
    @Getter
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime thoiGianBatDau;

    @Setter
    @Getter
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime thoiGianKetThuc;

    @Setter
    @Getter
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ngayMoBanVe;

    @Setter
    @Getter
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ngayDongBanVe;

    @Setter
    @Getter
    private MultipartFile anhBia;
    @Setter
    @Getter
    private String tenDiaDiem;
    @Setter
    @Getter
    private String maPhuongXa;
    @Setter
    @Getter
    private String[] maDanhMucs;
    @Setter
    @Getter
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
            // Handle null, empty, or the literal string "object"
            if (khuVucsJson == null || khuVucsJson.trim().isEmpty() || "object".equals(khuVucsJson)) {
                this.khuVucs = List.of();
                return;
            }

            System.out.println("KhuVucs JSON input: " + khuVucsJson);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String trimmed = khuVucsJson.trim();
            if (trimmed.startsWith("[")) {
                this.khuVucs = mapper.readValue(trimmed,
                        mapper.getTypeFactory().constructCollectionType(List.class, KhuVucRequest.class));
            } else if (trimmed.startsWith("{")) {
                KhuVucRequest singleKhuVuc = mapper.readValue(trimmed, KhuVucRequest.class);
                this.khuVucs = List.of(singleKhuVuc);
            } else {
                System.err.println("Invalid khuVucs format: " + khuVucsJson);
                this.khuVucs = List.of();
            }
        } catch (Exception e) {
            System.err.println("Error processing khuVucs: " + e.getMessage());
            e.printStackTrace();
            this.khuVucs = List.of(); // Use empty list instead of throwing exception
        }
    }
}