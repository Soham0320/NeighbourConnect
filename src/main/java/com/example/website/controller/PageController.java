package com.example.website.controller;

import com.example.website.dto.ContactForm;
import com.example.website.entity.Item;
import com.example.website.service.CategoryService;
import com.example.website.service.ContactService;
import com.example.website.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Public pages: home, listing, detail, contact. */
@Controller
public class PageController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ContactService contactService;

    public PageController(ItemService itemService, CategoryService categoryService, ContactService contactService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.contactService = contactService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", itemService.featured());
        model.addAttribute("categories", categoryService.findAll());
        return "index";
    }

    @GetMapping("/items")
    public String items(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Long category,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<Item> result = itemService.search(q, category, page);
        model.addAttribute("page", result);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", categoryService.findAll());
        return "items";
    }

    @GetMapping("/items/{slug}")
    public String itemDetail(@PathVariable String slug, Model model) {
        model.addAttribute("item", itemService.getBySlug(slug));
        return "item-detail";
    }

    @GetMapping("/contact")
    public String contactForm(Model model) {
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactForm());
        }
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactForm") ContactForm form,
                                BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "contact";
        }
        contactService.submit(form);
        redirect.addFlashAttribute("success", "Thanks! We'll get back to you soon.");
        return "redirect:/contact";
    }
}
