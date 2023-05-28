package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.*;
import com.alexdebur.TurMurom.Repositories.MarkRepository;
import com.alexdebur.TurMurom.Repositories.UserElectedMarkRepository;
import com.alexdebur.TurMurom.Services.*;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;

@Controller
public class MarkController {

    private static final int BUTTONS_TO_SHOW = 3;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 3;
    private static final int[] PAGE_SIZES = { 3, 6, 9, 12 };

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Marks\\";
    public static String ROUTE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Routes\\";


    private MarkService markService;
    private CategoryService categoryService;
    private ScheduleService scheduleService;
    private MarkPhotoService markPhotoService;
    private UserService userService;
    private final MarkRepository markRepository;
    private final UserElectedMarkRepository userElectedMarkRepository;

    public MarkController(MarkRepository markRepository,
                          UserElectedMarkRepository userElectedMarkRepository) {
        this.markRepository = markRepository;
        this.userElectedMarkRepository = userElectedMarkRepository;
    }

    @Autowired
    public void setServices(MarkService markService, CategoryService categoryService, UserService userService,
                            ScheduleService scheduleService, MarkPhotoService markPhotoService){
        this.markService = markService;
        this.userService = userService;
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

    @GetMapping("/places")
    public String getAll(Model model, @RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "9") int size,
                         @RequestParam(defaultValue = "title") String sortField,
                         @RequestParam(defaultValue = "asc") String sortDir, Principal principal,
                         @RequestParam(defaultValue = "card") String scheme) {
        try {
            if (keyword != null) {
                model.addAttribute("keyword", keyword);
            }

            Page<Mark> pageMarks = markService.listAll(page, size, keyword,sortField, sortDir);
            List<Mark> marks = pageMarks.getContent();

            if(principal != null){
                User user = userService.getUserByPrincipal(principal);
                model.addAttribute("user", user);
            }

            ArrayList<Long> list = new ArrayList<>();
            ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 2, 4));

            for (var mark : marks){
                for (var photo : mark.getMarkPhotos()){
                    try {
                        photo.setPathPhoto(InteractionPhoto.getPhoto(UPLOAD_DIRECTORY + photo.getPathPhoto()));
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

            model.addAttribute("marks", marks);
            model.addAttribute("activePage", "places");

            model.addAttribute("arr", arr);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", pageMarks.getTotalElements());
            model.addAttribute("totalPages", pageMarks.getTotalPages());
            model.addAttribute("pageSize", size);

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("scheme", scheme);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

        return "marks/places";
    }

    @GetMapping("/places/create")
    public String createMark(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer, Model model) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        fillModelWithMark(model, new Mark(), new Schedule(),
                new Schedule(), new Schedule(),
                new Schedule(), new Schedule(),
                new Schedule(), new Schedule());
        return "marks/insert";
    }

    @GetMapping("/places/details/{id}")
    public String detailsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Mark selectedMark = markService.getMarkById(id);
        List<String> photos = new ArrayList<>();
        for (var photo : selectedMark.getMarkPhotos()){
            try {
                photos.add(InteractionPhoto.getPhoto(UPLOAD_DIRECTORY + photo.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //List<File> photos = markService.getPhotos(selectedMark);
        List<Schedule> schedules = selectedMark.getSchedules();
        model.addAttribute("selectedMark", selectedMark);
        model.addAttribute("photos", photos);
        model.addAttribute("schedules", schedules);
        return "marks/mark_details";
    }

    @PostMapping("/places/addingMark")
    public String insertMark(Mark mark, String s1_start, String s1_end, String s3_start, String s3_end,
                             String s5_start, String s5_end, String s7_start, String s7_end,
                             String s9_start, String s9_end, String s11_start, String s11_end,
                             String s13_start, String s13_end, @RequestParam MultipartFile[] upload) throws IOException {
        mark.setElected(false);
        markService.insertMark(mark);

        Integer i = 1;
        for (var item: upload){
            MarkPhoto photo = new MarkPhoto();
            photo.setMark(mark);
            String fileName = "mark_" + mark.getTitle() + "_" +
                    (markService.getAllMarks().size() + 1) + "_" + i +
                    item.getOriginalFilename().substring(item.getOriginalFilename().length()-4);
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, item.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            photo.setPathPhoto(fileName);
            markPhotoService.insertMarkPhoto(photo);
            i++;
        }

        Mark mrk = markService.getMarkByTitleAndDescription(mark.getTitle(), mark.getDescription()).get();
        Schedule schedule1 = new Schedule();
        schedule1.setStart(s1_start);
        schedule1.setEnd(s1_end);
        schedule1.setDay(1);
        schedule1.setMark(mrk);
        scheduleService.insertSchedule(schedule1);
        Schedule schedule2 = new Schedule();
        schedule2.setStart(s3_start);
        schedule2.setEnd(s3_end);
        schedule2.setDay(2);
        schedule2.setMark(mrk);
        scheduleService.insertSchedule(schedule2);
        Schedule schedule3 = new Schedule();
        schedule3.setStart(s5_start);
        schedule3.setEnd(s5_end);
        schedule3.setDay(3);
        schedule3.setMark(mrk);
        scheduleService.insertSchedule(schedule3);
        Schedule schedule4 = new Schedule();
        schedule4.setStart(s7_start);
        schedule4.setEnd(s7_end);
        schedule4.setDay(4);
        schedule4.setMark(mrk);
        scheduleService.insertSchedule(schedule4);
        Schedule schedule5 = new Schedule();
        schedule5.setStart(s9_start);
        schedule5.setEnd(s9_end);
        schedule5.setDay(5);
        schedule5.setMark(mrk);
        scheduleService.insertSchedule(schedule5);
        Schedule schedule6 = new Schedule();
        schedule6.setStart(s11_start);
        schedule6.setEnd(s11_end);
        schedule6.setDay(6);
        schedule6.setMark(mrk);
        scheduleService.insertSchedule(schedule6);
        Schedule schedule7 = new Schedule();
        schedule7.setStart(s13_start);
        schedule7.setEnd(s13_end);
        schedule7.setDay(7);
        schedule7.setMark(mrk);
        scheduleService.insertSchedule(schedule7);
        return "redirect:/places";
    }

    @GetMapping("/places/edit/{id}")
    public String editMark(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                           Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        List<Schedule> schedules = scheduleService.getSchedulesByMark(markService.getMarkById(id));

        fillModelWithMark(model, markService.getMarkById(id), schedules.get(0),
                schedules.get(1), schedules.get(2),
                schedules.get(3), schedules.get(4),
                schedules.get(5), schedules.get(6));
        return "marks/edit";
    }

    @PostMapping("/places/editMark")
    public String editMark(Mark mark, String s1_start, String s1_end, String s3_start, String s3_end,
                           String s5_start, String s5_end, String s7_start, String s7_end,
                           String s9_start, String s9_end, String s11_start, String s11_end,
                           String s13_start, String s13_end, @RequestParam MultipartFile[] upload) throws IOException {

        if (upload[0].getOriginalFilename() != "") {

            String path;
            for (var photo : markService.getMarkById(mark.getId()).getMarkPhotos()) {
                path = "Marks\\" + photo.getPathPhoto();
                markPhotoService.deleteMarkPhotoById(photo.getId());

                InteractionPhoto.deletePhoto(path);
            }

            mark.setMarkPhotos(null);

            int i = 1;
            for (var item: upload){
                MarkPhoto photo = new MarkPhoto();
                photo.setMark(mark);
                String fileName = "mark_" + mark.getId() + "_" +
                        mark.getTitle() + "_" + i +
                        item.getOriginalFilename().substring(item.getOriginalFilename().length()-4);
                Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
                try {
                    Files.write(fileNameAndPath, item.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                photo.setPathPhoto(fileName);
                markPhotoService.insertMarkPhoto(photo);
                i++;
            }
        }

        mark.setElected(false);
        markService.insertMark(mark);

        Mark mrk = markService.getMarkByTitleAndDescription(mark.getTitle(), mark.getDescription()).get();
        List<Schedule> schedules = scheduleService.getSchedulesByMark(markService.getMarkById(mrk.getId()));

        schedules.get(0).setStart(s1_start);
        schedules.get(0).setEnd(s1_end);
        schedules.get(0).setDay(1);
        schedules.get(0).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(0));
        schedules.get(1).setStart(s3_start);
        schedules.get(1).setEnd(s3_end);
        schedules.get(1).setDay(2);
        schedules.get(1).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(1));
        schedules.get(2).setStart(s5_start);
        schedules.get(2).setEnd(s5_end);
        schedules.get(2).setDay(3);
        schedules.get(2).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(2));
        schedules.get(3).setStart(s7_start);
        schedules.get(3).setEnd(s7_end);
        schedules.get(3).setDay(4);
        schedules.get(3).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(3));
        schedules.get(4).setStart(s9_start);
        schedules.get(4).setEnd(s9_end);
        schedules.get(4).setDay(5);
        schedules.get(4).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(4));
        schedules.get(5).setStart(s11_start);
        schedules.get(5).setEnd(s11_end);
        schedules.get(5).setDay(6);
        schedules.get(5).setMark(mrk);
        scheduleService.insertSchedule(schedules.get(5));
        schedules.get(6).setStart(s13_start);
        schedules.get(6).setEnd(s13_end);
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
    public String deleteMarkById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 @PathVariable("id") Long id){
        Mark mark = markService.getMarkById(id);

        String path;
        for (var photo: mark.getMarkPhotos()){
            path = "Marks\\" + photo.getPathPhoto();
            InteractionPhoto.deletePhoto(path);
        }

        markService.deleteMarkById(id);
        return "redirect:" + referrer;
    }

    @PostMapping("/place/{markId}/elect/{locate}")
    public String electMark(Mark mark, Principal principal, @PathVariable("locate") String locate,
                                 @PathVariable("markId") Long markId) throws IOException {
        User user = userService.getUserByPrincipal(principal);
        Set<UserElectedMark> elected = user.getUserElectedMarks();

        UserElectedMark check = new UserElectedMark(user, markService.getMarkById(markId));

        if (elected.contains(check)){
            check = userElectedMarkRepository.findByUserIdAndMarkId(user.getId(), markId);
            elected.remove(check);
            markService.deleteElectedMark(check.getId());
        } else {
            elected.add(check);
        }

        user.setUserElectedMarks(elected);
        userService.editUser(user);

        if(locate.equals("places"))
            return "redirect:/places";
        else if(locate.equals("elected"))
            return "redirect:/profile/elected";
        else
            return "redirect:/routes/details/" + locate;
    }
}
