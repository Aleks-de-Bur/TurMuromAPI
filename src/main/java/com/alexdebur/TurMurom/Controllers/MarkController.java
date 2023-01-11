package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Category;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.MarkPhoto;
import com.alexdebur.TurMurom.Models.Schedule;
import com.alexdebur.TurMurom.Services.CategoryService;
import com.alexdebur.TurMurom.Services.MarkPhotoService;
import com.alexdebur.TurMurom.Services.MarkService;
import com.alexdebur.TurMurom.Services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Controller
public class MarkController {

    private static final int BUTTONS_TO_SHOW = 3;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 3;
    private static final int[] PAGE_SIZES = { 3, 6, 9, 12 };


    private MarkService markService;
    private CategoryService categoryService;
    private ScheduleService scheduleService;
    private MarkPhotoService markPhotoService;

    @Autowired
    public void setServices(MarkService markService, CategoryService categoryService,
                            ScheduleService scheduleService, MarkPhotoService markPhotoService){
        this.markService = markService;
        this.categoryService = categoryService;
        this.scheduleService = scheduleService;
        this.markPhotoService = markPhotoService;
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

    @RequestMapping("/places/{pageNum}")
    public String placesPage(Model model,
                             @PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir) {

        Page<Mark> page = markService.listAll(pageNum, sortField, sortDir);

        List<Mark> allMarks = page.getContent();
        model.addAttribute("marks", allMarks);
        model.addAttribute("activePage", "places");

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "marks/places";
    }



//    @RequestMapping("/page/{pageNum}")
//    public String viewPage(Model model,
//                           @PathVariable(name = "pageNum") int pageNum,
//                           @Param("sortField") String sortField,
//                           @Param("sortDir") String sortDir) {
//
//        Page<Product> page = service.listAll(pageNum, sortField, sortDir);
//
//        List<Product> listProducts = page.getContent();
//
//        model.addAttribute("currentPage", pageNum);
//        model.addAttribute("totalPages", page.getTotalPages());
//        model.addAttribute("totalItems", page.getTotalElements());
//
//        model.addAttribute("sortField", sortField);
//        model.addAttribute("sortDir", sortDir);
//        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
//
//        model.addAttribute("listProducts", listProducts);
//
//        return "index";
//    }


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
    public String insertMark(Mark mark, String s1_start, String s1_end, String s2_start, String s2_end,
                             String s3_start, String s3_end, String s4_start, String s4_end,
                             String s5_start, String s5_end, String s6_start, String s6_end,
                             String s7_start, String s7_end, @RequestParam MultipartFile[] upload) throws IOException {
        mark.setElected(false);
        markService.insertMark(mark);

        String path = System.getProperty("user.dir")+"/Photos/Marks";

        for (var item: upload){
            Path fileNameAndPath = Paths.get(path, item.getOriginalFilename());
            Files.write(fileNameAndPath, item.getBytes());

            MarkPhoto photo = new MarkPhoto();
            photo.setMark(mark);
            photo.setPathPhoto(item.getOriginalFilename());
            markPhotoService.insertMarkPhoto(photo);
        }

        Mark mrk = markService.getMarkByTitleAndDescription(mark.getTitle(), mark.getDescription()).get();
        Schedule schedule1 = new Schedule();
        schedule1.setStart(s1_start);
        schedule1.setEnd(s1_end);
        schedule1.setDay(1);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule1);
        Schedule schedule2 = new Schedule();
        schedule2.setStart(s2_start);
        schedule2.setEnd(s2_end);
        schedule2.setDay(2);
        schedule2.setMark(mrk);
        scheduleService.insertSchedule(schedule2);
        Schedule schedule3 = new Schedule();
        schedule3.setStart(s3_start);
        schedule3.setEnd(s3_end);
        schedule3.setDay(3);
        schedule3.setMark(mrk);
        scheduleService.insertSchedule(schedule3);
        Schedule schedule4 = new Schedule();
        schedule4.setStart(s4_start);
        schedule4.setEnd(s4_end);
        schedule4.setDay(4);
        schedule4.setMark(mrk);
        scheduleService.insertSchedule(schedule4);
        Schedule schedule5 = new Schedule();
        schedule5.setStart(s5_start);
        schedule5.setEnd(s5_end);
        schedule5.setDay(5);
        schedule5.setMark(mrk);
        scheduleService.insertSchedule(schedule5);
        Schedule schedule6 = new Schedule();
        schedule6.setStart(s6_start);
        schedule6.setEnd(s6_end);
        schedule6.setDay(6);
        schedule6.setMark(mrk);
        scheduleService.insertSchedule(schedule6);
        Schedule schedule7 = new Schedule();
        schedule7.setStart(s7_start);
        schedule7.setEnd(s7_end);
        schedule7.setDay(7);
        schedule7.setMark(mrk);
        scheduleService.insertSchedule(schedule7);
        return "redirect:/places/1?sortField=title&sortDir=asc";
    }

//    @PutMapping("/places/addingMark")
//    public String updateMark(@RequestBody Mark mark){
//        markService.insertMark(mark);
//        return "redirect:/marks/places";
//    }

    @GetMapping("/places/edit/{id}")
    public String editMark(Model model, @PathVariable("id") Long id) {
        List<Schedule> schedules = scheduleService.getSchedulesByMark(markService.getMarkById(id).get());

        fillModelWithMark(model, markService.getMarkById(id).get(), schedules.get(0),
                schedules.get(1), schedules.get(2),
                schedules.get(3), schedules.get(4),
                schedules.get(5), schedules.get(6));
        return "marks/edit";
    }

    @PostMapping("/places/editMark")
    public String editMark(Mark mark, String s1_start, String s1_end, String s2_start, String s2_end,
                             String s3_start, String s3_end, String s4_start, String s4_end,
                             String s5_start, String s5_end, String s6_start, String s6_end,
                             String s7_start, String s7_end, @RequestParam MultipartFile[] upload) throws IOException {
        mark.setElected(false);
        markService.insertMark(mark);

        String path = System.getProperty("user.dir")+"/MarkPhotos/";

        for (var item: upload){
            Path fileNameAndPath = Paths.get(path, item.getOriginalFilename());
            Files.write(fileNameAndPath, item.getBytes());

            MarkPhoto photo = new MarkPhoto();
            photo.setMark(mark);
            photo.setPathPhoto(item.getOriginalFilename());
            markPhotoService.insertMarkPhoto(photo);
        }

        Mark mrk = markService.getMarkByTitleAndDescription(mark.getTitle(), mark.getDescription()).get();
        List<Schedule> schedules = scheduleService.getSchedulesByMark(markService.getMarkById(mrk.getId()).get());

        schedules.get(0).setStart(s1_start);
        schedules.get(0).setEnd(s1_end);
        schedules.get(0).setDay(1);
        schedules.get(0).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(0));
        schedules.get(1).setStart(s2_start);
        schedules.get(1).setEnd(s2_end);
        schedules.get(1).setDay(2);
        schedules.get(1).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(1));
        schedules.get(2).setStart(s3_start);
        schedules.get(2).setEnd(s3_end);
        schedules.get(2).setDay(3);
        schedules.get(2).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(2));
        schedules.get(3).setStart(s4_start);
        schedules.get(3).setEnd(s4_end);
        schedules.get(3).setDay(4);
        schedules.get(3).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(3));
        schedules.get(4).setStart(s5_start);
        schedules.get(4).setEnd(s5_end);
        schedules.get(4).setDay(5);
        schedules.get(4).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(4));
        schedules.get(5).setStart(s6_start);
        schedules.get(5).setEnd(s6_end);
        schedules.get(5).setDay(6);
        schedules.get(5).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(5));
        schedules.get(6).setStart(s7_start);
        schedules.get(6).setEnd(s7_end);
        schedules.get(6).setDay(7);
        schedules.get(6).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(6));
        return "redirect:/places";
    }

//    @DeleteMapping("/places/delete/{id}")
//    public String deleteMarkById(@PathVariable("id") Long id){
//        markService.deleteMarkById(id);
//        return "redirect:/marks/places";
//    }

    @GetMapping("/places/delete/{id}")
    public String deleteMarkById(@PathVariable("id") Long id){
        markService.deleteMarkById(id);
        return "redirect:/places";
    }
}
