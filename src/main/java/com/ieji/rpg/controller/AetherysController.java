package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.aetherys.AetherysRequest;
import com.ieji.rpg.domain.dto.aetherys.AetherysResponse;
import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.AetherysService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aetherys")
@PreAuthorize("hasRole('USER')")
public class AetherysController extends AbstractController<Aetherys, Integer, AetherysRequest, AetherysResponse> {
    protected AetherysController(AetherysService service) {
        super(service);
    }
}
