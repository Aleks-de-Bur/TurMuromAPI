package com.alexdebur.TurMurom.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {
    @GetMapping("/")
    public String getHome(Model model){
        model.addAttribute("activePage", "home");
        return "index";
    }


//
//    @GetMapping("/routes")
//    public String getRoutes(Model model){
//        model.addAttribute("activePage", "routes");
//        return "routes/routes";
//    }
}
