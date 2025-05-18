package DuongVanBao.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {
    T save(T entity);
    List<T> saveAll(List<T> entities);
    Optional<T> findById(ID id);
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    void deleteById(ID id);
    void delete(T entity);
    boolean existsById(ID id);
    long count();
}
