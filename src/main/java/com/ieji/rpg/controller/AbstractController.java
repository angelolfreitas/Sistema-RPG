    package com.ieji.rpg.controller;

    import com.ieji.rpg.domain.dto.BaseDTO;
    import com.ieji.rpg.service.AbstractService;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;

    ///Abstracao para cruds mais simples,sem segurança
    public abstract class AbstractController<T, ID, DTO extends BaseDTO<ID>, DTI> {

        protected final AbstractService<T, ID, DTO, DTI> service;

        protected AbstractController(AbstractService<T, ID, DTO, DTI> service) {
            this.service = service;
        }

        @PostMapping
        public ResponseEntity<DTI> create(@RequestBody DTO dto) {
            return service.create(dto)
                    .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
        }

        @GetMapping
        public ResponseEntity<List<DTI>> findAll() {
            return ResponseEntity.ok(service.findAll());
        }
        @GetMapping("/{id}")
        public ResponseEntity<DTI> getById(@PathVariable ID id) {
            return ResponseEntity.ok(service.getById(id));
        }
        @PutMapping
        public ResponseEntity<DTI> update(@RequestBody DTO dto) {
            DTI response = service.update(dto);
            return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable ID id) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }

        @PatchMapping("/{id}")
        public ResponseEntity<DTI> patch(@PathVariable ID id,
                                         @RequestBody Map<String, Object> fields) {
            service.patchEntity(id, fields);
            return ResponseEntity.ok(service.getById(id));
        }
    }