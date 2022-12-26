package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Excursion;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Services.ExcursionService;
import com.alexdebur.TurMurom.Services.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ExcursionController {
    private ExcursionService excursionService;

    @Autowired
    public void setExcursionService(ExcursionService excursionService){
        this.excursionService = excursionService;
    }

    @GetMapping("/excursions")
    public String excursionsPage(Model model) {
        List<Excursion> allExcursions = excursionService.getAllExcursions();
        model.addAttribute("excursions", allExcursions);
        return "excursions/excursions";
    }

    @GetMapping("/excursion/details/{id}")
    public String detailsPage(Model model, @PathVariable("id") Long id) {
        Excursion selectedExcursion = excursionService.getExcursionById(id).get();
        model.addAttribute("selectedExcursion", selectedExcursion);
        return "excursions/details";
    }

    @GetMapping("/excursions/delete/{id}")
    public String deleteExcursionById(@PathVariable("id") Long id){
        excursionService.deleteExcursionById(id);
        return "redirect:/excursions/excursions";
    }
}
