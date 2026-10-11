package com.example.website.service;

import com.example.website.dto.ItemDto;
import com.example.website.entity.Category;
import com.example.website.entity.Item;
import com.example.website.exception.ResourceNotFoundException;
import com.example.website.repository.CategoryRepository;
import com.example.website.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class ItemService {

    private static final int PAGE_SIZE = 9;

    private final ItemRepository items;
    private final CategoryRepository categories;

    public ItemService(ItemRepository items, CategoryRepository categories) {
        this.items = items;
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public Page<Item> search(String q, Long categoryId, int page) {
        String query = q == null ? "" : q.trim();
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("createdAt").descending());
        return categoryId == null
                ? items.search(query, pageable)
                : items.searchByCategory(query, categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Item> featured() {
        return items.findTop6ByFeaturedTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return items.findAll(Sort.by("createdAt").descending());
    }

    @Transactional(readOnly = true)
    public Item getById(Long id) {
        return items.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Item getBySlug(String slug) {
        return items.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Item '" + slug + "' not found"));
    }

    @Transactional
    public Item create(ItemDto dto) {
        Item item = new Item();
        apply(item, dto);
        item.setSlug(uniqueSlug(dto.getTitle()));
        return items.save(item);
    }

    @Transactional
    public Item update(Long id, ItemDto dto) {
        Item item = getById(id);
        apply(item, dto);
        return items.save(item);
    }

    @Transactional
    public void delete(Long id) {
        items.delete(getById(id));
    }

    private void apply(Item item, ItemDto dto) {
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setImageUrl(dto.getImageUrl());
        item.setFeatured(dto.isFeatured());
        if (dto.getCategoryId() != null) {
            Category c = categories.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category " + dto.getCategoryId() + " not found"));
            item.setCategory(c);
        } else {
            item.setCategory(null);
        }
    }

    private String uniqueSlug(String title) {
        String base = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (base.isEmpty()) base = "item";
        String slug = base;
        int n = 2;
        while (items.existsBySlug(slug)) {
            slug = base + "-" + n++;
        }
        return slug;
    }

    public static ItemDto toDto(Item i) {
        ItemDto d = new ItemDto();
        d.setId(i.getId());
        d.setTitle(i.getTitle());
        d.setDescription(i.getDescription());
        d.setPrice(i.getPrice());
        d.setImageUrl(i.getImageUrl());
        d.setFeatured(i.isFeatured());
        d.setCategoryId(i.getCategory() == null ? null : i.getCategory().getId());
        return d;
    }
}
