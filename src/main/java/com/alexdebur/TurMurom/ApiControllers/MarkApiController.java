package com.alexdebur.TurMurom.ApiControllers;

import com.alexdebur.TurMurom.Models.Category;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Services.CategoryService;
import com.alexdebur.TurMurom.Services.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/api", produces="application/json")
@CrossOrigin(origins="*")
public class MarkApiController {
    private MarkService markService;
    private CategoryService categoryService;

    @Autowired
    public void setMarkService(MarkService markService, CategoryService categoryService){
        this.markService = markService;
        this.categoryService = categoryService;
    }

//    @GetMapping("/index")
//    public List<Mark> mainForm(){
//        return markService.getAllMarks();
//    }

//    @GetMapping("/catAPI/{id}")
//    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
//        Category cat = categoryService.getCategoryById(id).get();
//
//        Category category = new Category();
//        category.setId(cat.getId());
//        category.setTitle(cat.getTitle());
//
//        HttpHeaders headers = new HttpHeaders();
//
//        ResponseEntity<Category> entity = new ResponseEntity<>(category,headers, HttpStatus.CREATED);
//
//        return entity;
//    }

//    @GetMapping("/catAPI/{id}")
//    public Optional<Category> getCategory(@PathVariable Long id) {
//        Category cat = categoryService.getCategoryById(id).get();
//
//        Category category = new Category();
//        category.setId(cat.getId());
//        category.setTitle(cat.getTitle());
//
//
//        return Optional.of(category);
//    }
}
