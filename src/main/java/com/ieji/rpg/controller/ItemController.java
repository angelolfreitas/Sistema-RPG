package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.item.ItemRequest;
import com.ieji.rpg.domain.dto.item.ItemResponse;
import com.ieji.rpg.domain.entity.Item;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.ItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
@PreAuthorize("hasRole('USER')")
public class ItemController extends AbstractController<Item, Integer, ItemRequest, ItemResponse> {
    protected ItemController(ItemService service) {
        super(service);
    }
}
