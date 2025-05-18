package DuongVanBao.event.service;

import DuongVanBao.event.model.entity.DanhMucSuKien;
import java.util.List;

public interface DanhMucSuKienService extends BaseService<DanhMucSuKien, String> {
    boolean existsByIds(List<String> ids);
}
