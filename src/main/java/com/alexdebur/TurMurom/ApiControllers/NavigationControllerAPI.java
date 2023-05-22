package com.alexdebur.TurMurom.ApiControllers;

import com.alexdebur.TurMurom.Models.Category;
import com.alexdebur.TurMurom.Models.User;
import com.alexdebur.TurMurom.Repositories.UserRepository;
import com.alexdebur.TurMurom.Services.CategoryService;
import com.alexdebur.TurMurom.Services.MarkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NavigationControllerAPI {
    private MarkService markService;
    private CategoryService categoryService;

    @Autowired
    public void setMarkService(MarkService markService, CategoryService categoryService){
        this.markService = markService;
        this.categoryService = categoryService;
    }
    private final UserRepository userRepository;

    public NavigationControllerAPI(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/autorizedUser{email}")
    public User getUser(@PathVariable String email){
        return userRepository.findByEmail(email);
    }

    @GetMapping("/catAPI/{id}")
    public ObjectNode getCategory(@PathVariable Long id){
        Category cat = categoryService.getCategoryById(id).get();
        //return new Category(cat.getId(), cat.getTitle());

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", cat.getId());
        objectNode.put("title", cat.getTitle());
        return objectNode;
    }
}
