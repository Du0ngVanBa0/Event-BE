package DuongVanBao.event.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class EventRequest {
    private String tieuDe;
    private String moTa;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date thoiGianBatDau;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date thoiGianKetThuc;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date ngayMoBanVe;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date ngayDongBanVe;

    private MultipartFile anhBia;
    private String tenDiaDiem;
    private String maPhuongXa;
    private String[] maDanhMucs;
    private List<LoaiVeRequest> loaiVes;

    public void setLoaiVes(String loaiVesJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.loaiVes = mapper.readValue(loaiVesJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, LoaiVeRequest.class));
        } catch (Exception e) {
            throw new RuntimeException("Invalid loaiVes format");
        }
    }
}