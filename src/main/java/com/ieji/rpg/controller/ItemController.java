package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.item.ItemRequest;
import com.ieji.rpg.domain.dto.item.ItemResponse;
import com.ieji.rpg.domain.entity.Item;
import com.ieji.rpg.service.item.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/item")
@PreAuthorize("hasAuthority('user::write')")
public class ItemController extends AbstractController<Item, Integer, ItemRequest, ItemResponse> {
    protected ItemController(ItemService service) {
        super(service);
    }

    @PreAuthorize("hasAuthority('manager::write')")
    @Override
    public ResponseEntity<ItemResponse> create(@RequestBody ItemRequest dto) {
        return super.create(dto);
    }

    @PreAuthorize("hasAuthority('manager::write')")
    @Override
    public ResponseEntity<ItemResponse> update(@RequestBody ItemRequest dto) {
        return super.update(dto);
    }

    @PreAuthorize("hasAuthority('manager::write')")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return super.delete(id);
    }

    @PreAuthorize("hasAuthority('manager::write')")
    @Override
    public ResponseEntity<ItemResponse> patch(@PathVariable Integer id, @RequestBody Map<String, Object> fields) {
        return super.patch(id, fields);
    }
}
