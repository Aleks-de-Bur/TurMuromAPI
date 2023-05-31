package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.User;
import com.alexdebur.TurMurom.Models.UserElectedMark;
import com.alexdebur.TurMurom.Services.*;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.alexdebur.TurMurom.Controllers.MarkController.UPLOAD_DIRECTORY;

@Controller
public class MapController {

    private MarkService markService;
    private UserService userService;

    @Autowired
    public void setServices(MarkService markService, UserService userService){
        this.markService = markService;
        this.userService = userService;
    }

    @GetMapping("/map")
    public String getMap(Model model, Principal principal){
        List<Mark> allMarks = markService.getAllMarks();

        if(principal != null){
            User user = userService.getUserByPrincipal(principal);
            List<Mark> userElectedMarks = markService.getElectedMarks(user.getId());
            model.addAttribute("userElectedMarks", userElectedMarks);
            model.addAttribute("user", user);
        } else {
            //List<Mark> list = new ArrayList<>();
            model.addAttribute("userElectedMarks", new ArrayList<>());
            model.addAttribute("user", "");
        }

        for (var mark : allMarks){
            for (var photo : mark.getMarkPhotos()){
                try {
                    photo.setPathPhoto(InteractionPhoto.getPhoto(UPLOAD_DIRECTORY + photo.getPathPhoto()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        model.addAttribute("marks", allMarks);
        model.addAttribute("activePage", "map");
        return "map/map";
    }
}
