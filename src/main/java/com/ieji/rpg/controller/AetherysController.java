package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.aetherys.AetherysRequest;
import com.ieji.rpg.domain.dto.aetherys.AetherysResponse;
import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.service.aetherys.AetherysService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/aetherys")
@PreAuthorize("hasAuthority('user::write')")
public class AetherysController extends AbstractController<Aetherys, Integer, AetherysRequest, AetherysResponse> {
    protected AetherysController(AetherysService service) {
        super(service);
    }
    @Override
    @PostMapping
    @PreAuthorize("hasAnyAuthority('manager::write', 'admin::write')")
    public ResponseEntity<AetherysResponse> create(@RequestBody AetherysRequest dto) {
        return service.create(dto)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
    @PreAuthorize("hasAnyAuthority('manager::write', 'admin::write')")
    @Override
    public ResponseEntity<AetherysResponse> update(@RequestBody AetherysRequest dto) {
        return super.update(dto);
    }

    @PreAuthorize("hasAuthority('admin::write')")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return super.delete(id);
    }

    @PreAuthorize("hasAnyAuthority('manager::write', 'admin::write')")
    @Override
    public ResponseEntity<AetherysResponse> patch(@PathVariable Integer id, @RequestBody Map<String, Object> fields) {
        return super.patch(id, fields);
    }
}
