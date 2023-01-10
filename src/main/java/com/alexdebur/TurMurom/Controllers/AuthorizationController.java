package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Category;
import com.alexdebur.TurMurom.Models.RegisterEntity;
import com.alexdebur.TurMurom.Services.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthorizationController {
    private AuthorizationService authorizationService;

    @Autowired
    public void setServices(AuthorizationService authorizationService){
        this.authorizationService = authorizationService;
    }

    private void fillModelWithUser(Model model, RegisterEntity user){
        model.addAttribute("user", user);
    }

    @GetMapping("/login")
    public String logInPage(Model model) {

        return "authorization/log_in";
    }

    @GetMapping("/sign_up")
    public String signUpPage(Model model) {
        fillModelWithUser(model, new RegisterEntity());
        return "authorization/sign_up";
    }

    @PostMapping("/sign_up/confirm")
    public String insertUser(RegisterEntity registerEntity){
        authorizationService.insertUser(registerEntity);
        return "redirect:/";
    }
}
