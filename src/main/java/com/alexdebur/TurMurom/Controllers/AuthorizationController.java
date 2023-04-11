package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.UserRegistrationDto;
import com.alexdebur.TurMurom.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/sign_up")
public class AuthorizationController {
    @Autowired
    private UserService userService;

    public AuthorizationController(UserService userService) {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "authorization/sign_up";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        userService.save(registrationDto);
        return "redirect:/log_in";
    }
}


//    private AuthorizationService authorizationService;
//
//    @Autowired
//    public void setServices(AuthorizationService authorizationService){
//        this.authorizationService = authorizationService;
//    }
//
//    private void fillModelWithUser(Model model, RegisterEntity user){
//        model.addAttribute("user", user);
//    }
//
//    @GetMapping("/login")
//    public String logInPage(Model model) {
//        public String logInPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
//        Model model) {
//
//            if (referrer != null) {
//                model.addAttribute("previousUrl", referrer);
//            }
//
//            return "authorization/log_in";
//        }
//}
