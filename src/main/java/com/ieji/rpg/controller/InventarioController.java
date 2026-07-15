package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.inventario.InventarioRequest;
import com.ieji.rpg.domain.dto.inventario.InventarioResponse;
import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.InventarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventario")
@PreAuthorize("hasRole('USER')")
public class InventarioController  extends AbstractController<Inventario, InventarioId, InventarioRequest, InventarioResponse> {

    protected InventarioController(InventarioService service) {
        super(service);
    }
}
