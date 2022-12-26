package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Category;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.Schedule;
import com.alexdebur.TurMurom.Services.CategoryService;
import com.alexdebur.TurMurom.Services.MarkService;
import com.alexdebur.TurMurom.Services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@Controller
public class MarkController {
    private MarkService markService;
    private CategoryService categoryService;
    private ScheduleService scheduleService;

    @Autowired
    public void setServices(MarkService markService, CategoryService categoryService,
                            ScheduleService scheduleService){
        this.markService = markService;
        this.categoryService = categoryService;
        this.scheduleService = scheduleService;
    }

    private void fillModelWithMark(Model model, Mark mark, Schedule schedule1,
                                   Schedule schedule2, Schedule schedule3,
                                   Schedule schedule4, Schedule schedule5,
                                   Schedule schedule6, Schedule schedule7){
        List<Category> allCategories = categoryService.getAllCategories();
        model.addAttribute("mark", mark);
        model.addAttribute("categories", allCategories);
        model.addAttribute("schedule1", schedule1);
        model.addAttribute("schedule2", schedule2);
        model.addAttribute("schedule3", schedule3);
        model.addAttribute("schedule4", schedule4);
        model.addAttribute("schedule5", schedule5);
        model.addAttribute("schedule6", schedule6);
        model.addAttribute("schedule7", schedule7);
    }

    @GetMapping("/places")
    public String placesPage(Model model) {
        List<Mark> allMarks = markService.getAllMarks();
        model.addAttribute("marks", allMarks);
        model.addAttribute("activePage", "places");
        return "marks/places";
    }

    @GetMapping("/places/create")
    public String createMark(Model model) {
        fillModelWithMark(model, new Mark(), new Schedule(),
                new Schedule(), new Schedule(),
                new Schedule(), new Schedule(),
                new Schedule(), new Schedule());
        return "marks/insert";
    }

    @GetMapping("/places/details/{id}")
    public String detailsPage(Model model, @PathVariable("id") Long id) {
        Mark selectedMark = markService.getMarkById(id).get();
        List<File> photos = markService.getPhotos(selectedMark);
        List<Schedule> schedules = selectedMark.getSchedules();
        model.addAttribute("selectedMark", selectedMark);
        model.addAttribute("schedules", schedules);
        return "marks/mark_details";
    }

    @PostMapping("/places/addingMark")
    public String insertMark(@RequestBody Mark mark, @RequestBody Schedule schedule1,
                             @RequestBody Schedule schedule2, @RequestBody Schedule schedule3,
                             @RequestBody Schedule schedule4, @RequestBody Schedule schedule5,
                             @RequestBody Schedule schedule6, @RequestBody Schedule schedule7){
        mark.setElected(false);
        markService.insertMark(mark);
        Mark mrk = markService.getMarkByTitleAndDescription(mark.getTitle(), mark.getDescription()).get();
        schedule1.setDay(1);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule1);
        schedule1.setDay(2);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule2);
        schedule1.setDay(3);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule3);
        schedule1.setDay(4);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule4);
        schedule1.setDay(5);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule5);
        schedule1.setDay(6);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule6);
        schedule1.setDay(7);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule7);
        return "redirect:/marks/places";
    }

//    @PutMapping("/places/addingMark")
//    public String updateMark(@RequestBody Mark mark){
//        markService.insertMark(mark);
//        return "redirect:/marks/places";
//    }

    @DeleteMapping("/places/delete/{id}")
    public String deleteMarkById(@PathVariable("id") Long id){
        markService.deleteMarkById(id);
        return "redirect:/marks/places";
    }
}
