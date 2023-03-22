package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Services.CategoryService;
import com.alexdebur.TurMurom.Services.MarkPhotoService;
import com.alexdebur.TurMurom.Services.MarkService;
import com.alexdebur.TurMurom.Services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MapController {

    private MarkService markService;

    @Autowired
    public void setServices(MarkService markService){
        this.markService = markService;
    }

    @GetMapping("/map")
    public String getMap(Model model){
        List<Mark> allMarks = markService.getAllMarks();

        model.addAttribute("marks", allMarks);
        model.addAttribute("activePage", "map");
        return "map/map";
    }
}
