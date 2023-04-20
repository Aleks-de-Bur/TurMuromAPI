package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.Role;
import com.alexdebur.TurMurom.Models.User;
import com.alexdebur.TurMurom.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
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

    @GetMapping("/admin_cabinet/user/edit/{user}")
    public String userEdit(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute("user", user);
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        model.addAttribute("roles", Role.values());
        return "authorization/user-edit";
    }

    @PostMapping("/admin_cabinet/user/edit")
    public String userEdit(@RequestParam("userId") User user, @RequestParam Map<String, String> form) {
        userService.changeUserRoles(user, form);
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

    @GetMapping("/admin/get/{userId}")
    public String getUser(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute("user", user);
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "authorization/personal_cabinet";
    }
}