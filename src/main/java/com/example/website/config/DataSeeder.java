package com.example.website.config;

import com.example.website.dto.ItemDto;
import com.example.website.entity.Category;
import com.example.website.entity.Role;
import com.example.website.repository.CategoryRepository;
import com.example.website.repository.ItemRepository;
import com.example.website.service.AppUserService;
import com.example.website.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/** Creates the admin account and a few sample rows on first start. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserService users;
    private final CategoryRepository categories;
    private final ItemRepository itemRepo;
    private final ItemService itemService;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;

    public DataSeeder(AppUserService users, CategoryRepository categories,
                      ItemRepository itemRepo, ItemService itemService) {
        this.users = users;
        this.categories = categories;
        this.itemRepo = itemRepo;
        this.itemService = itemService;
    }

    @Override
    public void run(String... args) {
        if (!users.emailExists(adminEmail)) {
            users.create("Administrator", adminEmail, adminPassword, Role.ADMIN);
        }
        if (categories.count() == 0) {
            Category a = categories.save(new Category("Category A", "category-a", "Sample category A"));
            Category b = categories.save(new Category("Category B", "category-b", "Sample category B"));
            if (itemRepo.count() == 0) {
                for (int i = 1; i <= 8; i++) {
                    ItemDto d = new ItemDto();
                    d.setTitle("Sample Item " + i);
                    d.setDescription("Replace this with real content for item " + i + ".");
                    d.setPrice(new BigDecimal(10 * i));
                    d.setFeatured(i <= 4);
                    d.setCategoryId(i % 2 == 0 ? a.getId() : b.getId());
                    itemService.create(d);
                }
            }
        }
    }
}
