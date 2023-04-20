package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.UserRegistrationDto;
import com.alexdebur.TurMurom.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
//    @Autowired
//    private UserService service;
//
//    @GetMapping("/log_in")
//    public String iniciarSesion() {
//        return "authorization/log_in";
//    }
//
//    @GetMapping("/personal_cabinet")
//    public String personalCabinet() {
//        return "authorization/personal_cabinet";
//    }
//
//    @GetMapping("/admin")
//    public String verPaginaDeInicio(Model modelo) {
//        modelo.addAttribute("users", service.getAll());
//        return "authorization/example";
//    }
//
//    @PostMapping("/log_in")
//    public String redirectUserAccount() {
//        return "redirect:/";
//    }
}
