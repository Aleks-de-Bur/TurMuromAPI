package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Guide;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.Role;
import com.alexdebur.TurMurom.Models.User;
import com.alexdebur.TurMurom.Services.GuideService;
import com.alexdebur.TurMurom.Services.UserService;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.alexdebur.TurMurom.Services.UserService.UPLOAD_DIRECTORY;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
    private final GuideService guideService;
    private static final int BUTTONS_TO_SHOW = 3;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 3;
    private static final int[] PAGE_SIZES = { 3, 6, 9, 12 };

    @GetMapping("/admin_cabinet/{pageNum}")
    public String admin(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                        Model model, @PathVariable(name = "pageNum") int pageNum,
                        Principal principal,
                        @Param("sortField") String sortField,
                        @Param("sortDir") String sortDir) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Page<User> page = userService.listAll(pageNum, sortField, sortDir);
        List<User> allUsers = page.getContent();

        //model.addAttribute("users", userService.list());
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        model.addAttribute("roles", Role.values());

        model.addAttribute("users", allUsers);
        model.addAttribute("activePage", "admin");

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "authorization/admin";
    }

    @PostMapping("/admin_cabinet/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id) {
        userService.banUser(id);
        return "redirect:/admin_cabinet/1?sortField=lastName&sortDir=asc";
    }

//    @GetMapping("/admin_cabinet/user/edit/{user}")
//    public String userEdit(@PathVariable("user") User user, Model model, Principal principal) {
//        model.addAttribute("user", user);
////        model.addAttribute("user", userService.getUserByPrincipal(principal));
//        model.addAttribute("roles", Role.values());
//        return "authorization/user-edit";
//    }

    @PostMapping("/admin_cabinet/user/edit")
    public String userEdit(@RequestParam("userId") User user, @RequestParam Map<String, String> form) {
        userService.changeUserRoles(user, form);
        return "redirect:/personal_cabinet/admin";
    }
    @PostMapping("/admin_cabinet/user/delete/{id}")
    public String userDelete(@RequestParam("Id") Long id, @RequestParam Map<String, String> form) {
        userService.deleteUserById(id);
        return "redirect:/personal_cabinet/admin";
    }

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

    @GetMapping("/admin_cabinet/user/{userId}")
    public String getUser(Model model, @PathVariable("userId") Long id) {
        User user = userService.getUserById(id);
        String photo = UPLOAD_DIRECTORY + user.getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(user.getGuideId() != null){
            Guide guide = guideService.getGuideById(user.getGuideId()).get();
            model.addAttribute("guide", guide);
        } else model.addAttribute("guide", "");

        model.addAttribute("user", user);
        model.addAttribute("photo", photo);
        return "authorization/personal_cabinet";
    }
}