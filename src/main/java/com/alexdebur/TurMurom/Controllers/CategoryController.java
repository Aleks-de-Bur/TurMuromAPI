package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Category;
import com.alexdebur.TurMurom.Services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    public void setServices(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    private void fillModelWithCategory(Model model, Category category){
        model.addAttribute("category", category);
    }

    @GetMapping("/categories")
    public String placesPage(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("activePage", "categories");
        return "categories/categories";
    }

    @GetMapping("/categories/create")
    public String createCategory(Model model) {
        fillModelWithCategory(model, new Category());
        return "categories/insert";
    }

    @GetMapping("/categories/details/{id}")
    public String detailsPage(Model model, @PathVariable("id") Long id) {
        Category selectedCategory = categoryService.getCategoryById(id).get();

        model.addAttribute("selectedCategory", selectedCategory);

        return "categories/details";
    }

    @GetMapping("/categories/edit/{id}")
    public String editPage(Model model, @PathVariable("id") Long id) {
        Category selectedCategory = categoryService.getCategoryById(id).get();

        model.addAttribute("selectedCategory", selectedCategory);

        return "categories/edit";
    }

    @PostMapping("/categories/addingCategory")
    public String insertCategory(Category category){
        categoryService.insertCategory(category);
        return "redirect:/categories";
    }

    @PostMapping("/categories/editCategory")
    public String editCategory(Category category){
        categoryService.insertCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategoryById(@PathVariable("id") Long id){
        categoryService.deleteCategoryById(id);
        return "redirect:/categories";
    }
}
