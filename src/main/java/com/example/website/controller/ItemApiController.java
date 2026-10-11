package com.example.website.controller;

import com.example.website.dto.ItemDto;
import com.example.website.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** JSON API. GET is public; POST/PUT/DELETE require ROLE_ADMIN. */
@RestController
@RequestMapping("/api/items")
public class ItemApiController {

    private final ItemService itemService;

    public ItemApiController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Page<ItemDto> list(@RequestParam(required = false) String q,
                              @RequestParam(required = false) Long category,
                              @RequestParam(defaultValue = "0") int page) {
        return itemService.search(q, category, page).map(ItemService::toDto);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id) {
        return ItemService.toDto(itemService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@Valid @RequestBody ItemDto dto) {
        return ItemService.toDto(itemService.create(dto));
    }

    @PutMapping("/{id}")
    public ItemDto update(@PathVariable Long id, @Valid @RequestBody ItemDto dto) {
        return ItemService.toDto(itemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
