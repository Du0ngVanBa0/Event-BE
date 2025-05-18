package DuongVanBao.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseModel {
    @Column(name = "ngayTao", updatable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
}
