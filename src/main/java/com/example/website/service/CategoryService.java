package com.example.website.service;

import com.example.website.entity.Category;
import com.example.website.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categories;

    public CategoryService(CategoryRepository categories) {
        this.categories = categories;
    }

    public List<Category> findAll() {
        return categories.findAll();
    }
}
