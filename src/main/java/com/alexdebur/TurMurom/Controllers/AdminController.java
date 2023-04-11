package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    @Autowired
    private UserService userService;

//    @GetMapping("/personal_cabinet")
//    public String userList(Model model) {
//        //model.addAttribute("allUsers", userService.allUsers());
//        return "authorization/personal_cabinet";
//    }

//    @PostMapping("/personal_cabinet")
//    public String  deleteUser(@RequestParam(required = true, defaultValue = "" ) Long userId,
//                              @RequestParam(required = true, defaultValue = "" ) String action,
//                              Model model) {
//        if (action.equals("delete")){
//            //userService.deleteUser(userId);
//        }
//        return "redirect:/authorization/personal_cabinet";
//    }

    @GetMapping("/personal_cabinet/gt/{userId}")
    public String  gtUser(@PathVariable("userId") Long userId, Model model) {
        //model.addAttribute("allUsers", userService.usergtList(userId));
        return "authorization/personal_cabinet";
    }
}