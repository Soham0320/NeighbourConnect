package com.example.website.controller;

import com.example.website.dto.ItemDto;
import com.example.website.service.CategoryService;
import com.example.website.service.ContactService;
import com.example.website.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Minimal admin dashboard (ROLE_ADMIN only, enforced in SecurityConfig). */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ContactService contactService;

    public AdminController(ItemService itemService, CategoryService categoryService, ContactService contactService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.contactService = contactService;
    }

    @GetMapping
    public String dashboard(Model model) {
        if (!model.containsAttribute("itemForm")) {
            model.addAttribute("itemForm", new ItemDto());
        }
        return populate(model);
    }

    @PostMapping("/items")
    public String createItem(@Valid @ModelAttribute("itemForm") ItemDto form,
                             BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return populate(model);
        }
        itemService.create(form);
        redirect.addFlashAttribute("success", "Item created");
        return "redirect:/admin";
    }

    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirect) {
        itemService.delete(id);
        redirect.addFlashAttribute("success", "Item deleted");
        return "redirect:/admin";
    }

    @PostMapping("/messages/{id}/handled")
    public String markHandled(@PathVariable Long id) {
        contactService.markHandled(id);
        return "redirect:/admin";
    }

    private String populate(Model model) {
        model.addAttribute("items", itemService.findAll());
        model.addAttribute("messages", contactService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/dashboard";
    }
}
