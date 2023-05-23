package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Excursion;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.User;
import com.alexdebur.TurMurom.Services.ExcursionService;
import com.alexdebur.TurMurom.Services.MarkService;
import com.alexdebur.TurMurom.Services.UserService;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorizationController {
    private final UserService userService;
    private final MarkService markService;
    private final ExcursionService excursionService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Users\\";
    public static String MARK_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Marks\\";
    public static String ROUTE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Routes\\";
    public static String EXCURSION_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Excursions\\";
    public static String GUIDE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Guides\\";

    @GetMapping("/log_in")
    public String login(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                        Principal principal, Model model) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "authorization/log_in";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
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

    @GetMapping("/profile/elected")
    public String elected(Principal principal, Model model) {
        User user = userService.getUserByPrincipal(principal);

        List<Mark> marks = markService.getElectedMarks(user.getId());
        List<Excursion> excursions = excursionService.getElectedExcursions(user.getId());

        ArrayList<Long> list = new ArrayList<>();
        ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 3, 6));

        for (var mark : marks){
            for (var photo : mark.getMarkPhotos()){
                try {
                    photo.setPathPhoto(InteractionPhoto.getPhoto(MARK_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for (var routeMarks : mark.getRouteMarks()) {
                if (!list.contains(routeMarks.getRoute().getId())) {
                    try {
                        routeMarks.getRoute().setPathPhoto(InteractionPhoto.getPhoto(ROUTE_UPLOAD_DIRECTORY + routeMarks.getRoute().getPathPhoto()));
                        list.add(routeMarks.getRoute().getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        list.clear();

        for (var excursion : excursions){
            for (var photo : excursion.getExcursionPhotos()){
                try {
                    photo.setPathPhoto(InteractionPhoto.getPhoto(EXCURSION_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!list.contains(excursion.getGuide().getId())) {
                try {
                    excursion.getGuide().setPathPhoto(InteractionPhoto
                            .getPhoto(GUIDE_UPLOAD_DIRECTORY + excursion.getGuide().getPathPhoto()));
                    list.add(excursion.getGuide().getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        model.addAttribute("marks", marks);
        model.addAttribute("excursions", excursions);
        model.addAttribute("arr", arr);

        return "authorization/elected";
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
        model.addAttribute("errorMessage", "");
//        return "authorization/registration";
        return "authorization/sign_up";
    }

    @PostMapping("/sign_up")
    public String createUser(User user, Model model, @RequestParam("image") MultipartFile file) throws IOException {
        String fileName = user.getLastName() + "_" + user.getFirstName() + "_" +
                file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

        user.setPathPhoto(fileName);

        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с email: " + user.getEmail() + " уже существует");
//            return "authorization/registration";
            return "authorization/sign_up";
        }

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
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