package DuongVanBao.event.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface BaseController<T, ID> {
    @GetMapping
    ResponseEntity<?> getAll();

    @GetMapping("/page")
    ResponseEntity<?> getPage(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<?> getById(@PathVariable ID id);

    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody T entity);

    @PutMapping("/{id}")
    ResponseEntity<?> update(@PathVariable ID id, @Valid @RequestBody T entity);

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable ID id);
}
