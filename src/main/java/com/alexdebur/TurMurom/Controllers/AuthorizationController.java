package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.User;
import com.alexdebur.TurMurom.Services.UserService;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AuthorizationController {
    private final UserService userService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Users\\";

    @GetMapping("/log_in")
    public String login(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "authorization/log_in";
    }

    @GetMapping("/profile")
    public String profile(Principal principal,
                          Model model) {
        User user = userService.getUserByPrincipal(principal);
        String photo = UPLOAD_DIRECTORY + user.getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("user", user);
        model.addAttribute("photo", photo);
        return "authorization/personal_cabinet";
    }

    @PostMapping("/profile/edit")
    public String editProfile(User user, @RequestParam("image") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            String fileName = user.getLastName() + "_" + user.getId() + "_" +
                    file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            user.setPathPhoto(fileName);
        }
        userService.editUser(user);
        return "redirect:/profile";
    }

    @GetMapping("/sign_up")
    public String registration(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "authorization/registration";
//        return "authorization/sign_up";
    }


    @PostMapping("/sign_up")
    public String createUser(User user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с email: " + user.getEmail() + " уже существует");
            return "authorization/registration";
//            return "authorization/sign_up";
        }
        return "redirect:/log_in";
    }

    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute("user", user);
        model.addAttribute("userByPrincipal", userService.getUserByPrincipal(principal));
        return "authorization/user_info";
    }
}