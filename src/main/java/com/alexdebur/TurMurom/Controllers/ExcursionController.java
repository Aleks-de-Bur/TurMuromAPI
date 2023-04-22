package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.*;
import com.alexdebur.TurMurom.Services.ExcursionPhotoService;
import com.alexdebur.TurMurom.Services.ExcursionService;
import com.alexdebur.TurMurom.Services.GuideService;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class ExcursionController {
    private ExcursionService excursionService;
    private ExcursionPhotoService excursionPhotoService;
    private GuideService guideService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Excursions\\";
    public String GUIDE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Guides\\";

    @Autowired
    public void setExcursionService(ExcursionService excursionService, ExcursionPhotoService excursionPhotoService,
                                    GuideService guideService) {
        this.excursionService = excursionService;
        this.excursionPhotoService = excursionPhotoService;
        this.guideService = guideService;
    }

    private void fillModelWithExcursion(Model model, Excursion excursion, List<ExcursionPhoto> photos) {
        model.addAttribute("excursion", excursion);
        model.addAttribute("photos", photos);
    }

    private void fillModelWithNewExcursion(Model model, Excursion excursion, Long guideId) {
        model.addAttribute("excursion", excursion);
        model.addAttribute("guideId", guideId);
    }

    @GetMapping("/excursions")
    public String excursionsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 Model model, @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "9") int size,
                                 @RequestParam(defaultValue = "title") String sortField,
                                 @RequestParam(defaultValue = "asc") String sortDir,
                                 @RequestParam(defaultValue = "card") String scheme) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        try {
            if (keyword != null) {
                model.addAttribute("keyword", keyword);
            }

            Page<Excursion> pageExcursions = excursionService.listAll(page, size, keyword,sortField, sortDir);
            List<Excursion> excursions = pageExcursions.getContent();

            ArrayList<Long> list = new ArrayList<>();

            for (var excursion : excursions){
                for (var photo : excursion.getExcursionPhotos()){
                    try {
                        photo.setPathPhoto(InteractionPhoto.getPhoto(UPLOAD_DIRECTORY + photo.getPathPhoto()));
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

            model.addAttribute("excursions", excursions);
            model.addAttribute("activePage", "excursions");

            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", pageExcursions.getTotalElements());
            model.addAttribute("totalPages", pageExcursions.getTotalPages());
            model.addAttribute("pageSize", size);

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("scheme", scheme);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }



        //List<Excursion> allExcursions = excursionService.getAllExcursions();

//        Page<Excursion> page = excursionService.listAll(pageNum, sortField, sortDir);
//        List<Excursion> allExcursions = page.getContent();
//
//        ArrayList<Long> list = new ArrayList<>();
//
//        for (var excursion : allExcursions){
//            for (var photo : excursion.getExcursionPhotos()){
//                try {
//                    photo.setPathPhoto(InteractionPhoto.getPhoto(UPLOAD_DIRECTORY + photo.getPathPhoto()));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if (!list.contains(excursion.getGuide().getId())) {
//                try {
//                    excursion.getGuide().setPathPhoto(InteractionPhoto
//                            .getPhoto(GUIDE_UPLOAD_DIRECTORY + excursion.getGuide().getPathPhoto()));
//                    list.add(excursion.getGuide().getId());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//
//        model.addAttribute("excursions", allExcursions);
//        model.addAttribute("activePage", "excursions");
//
//        model.addAttribute("currentPage", pageNum);
//        model.addAttribute("totalPages", page.getTotalPages());
//        model.addAttribute("totalItems", page.getTotalElements());
//
//        model.addAttribute("sortField", sortField);
//        model.addAttribute("sortDir", sortDir);
//        model.addAttribute("scheme", scheme);
//        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "excursions/excursions";
    }

    @GetMapping("/guides/edit/{guideId}/excursion/create")
    public String createExcursion(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                  Model model, @PathVariable("guideId") Long guideId) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        fillModelWithNewExcursion(model, new Excursion(), guideId);
        return "excursions/insert";
    }

    @PostMapping("/guides/edit/{guideId}/excursion/create/done")
    public String insertExcursion(Excursion excursion, @PathVariable("guideId") Long guideId,
                                  @RequestParam MultipartFile[] upload) throws IOException {
        excursion.setGuide(guideService.getGuideById(guideId).get());
        excursionService.insertExcursion(excursion);

        String path = System.getProperty("user.dir") + "/Photos/Excursions";

        for (var item : upload) {
            Path fileNameAndPath = Paths.get(path, item.getOriginalFilename());
            Files.write(fileNameAndPath, item.getBytes());

            ExcursionPhoto photo = new ExcursionPhoto();
            photo.setExcursion(excursion);
            photo.setPathPhoto(item.getOriginalFilename());
            excursionPhotoService.insertExcursionPhoto(photo);
        }
        return "redirect:/guides/details/" + guideId;
    }

    @GetMapping("/guides/edit/{guideId}/excursion/{excursionId}/details")
    public String detailsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("excursionId") Long excursionId) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Excursion selectedExcursion = excursionService.getExcursionById(excursionId).get();
        List<String> photos = new ArrayList<>();
        for (var photo : selectedExcursion.getExcursionPhotos()){
            try {
                photos.add(InteractionPhoto.getPhoto(UPLOAD_DIRECTORY + photo.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        model.addAttribute("selectedExcursion", selectedExcursion);
        model.addAttribute("photos", photos);
        return "excursions/details";
    }

    @GetMapping("/guides/edit/{guideId}/excursion/{excursionId}/edit")
    public String editExcursion(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                Model model, @PathVariable("guideId") Long guideId,
                                @PathVariable("excursionId") Long excursionId) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        List<ExcursionPhoto> photos = excursionService.getExcursionById(excursionId).get().getExcursionPhotos();

        fillModelWithExcursion(model, excursionService.getExcursionById(excursionId).get(), photos);
        return "excursions/edit";
    }

    @PostMapping("/guides/edit/{guideId}/excursion/{excursionId}/done")
    public String editExcursion(Excursion excursion, @RequestParam MultipartFile[] upload,
                                @PathVariable("guideId") Long guideId) throws IOException {
        excursion.setGuide(guideService.getGuideById(guideId).get());
        excursionService.insertExcursion(excursion);

        if (upload[0].getOriginalFilename() != "") {
            String path = System.getProperty("user.dir") + "/Photos/Excursions/";

            for (var item : upload) {
                Path fileNameAndPath = Paths.get(path, item.getOriginalFilename());
                Files.write(fileNameAndPath, item.getBytes());

                ExcursionPhoto photo = new ExcursionPhoto();
                photo.setExcursion(excursion);
                photo.setPathPhoto(item.getOriginalFilename());
                excursionPhotoService.insertExcursionPhoto(photo);
            }
        }
        return "redirect:/guides/edit/" + guideId;
    }

    @GetMapping("/guides/edit/{guideId}/excursion/{excursionId}/delete")
    public String deleteExcursionById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                      @PathVariable("excursionId") Long excursionId,
                                      @PathVariable("guideId") Long guideId) {

        Excursion excursion = excursionService.getExcursionById(excursionId).get();

        String path;
        for (var photo: excursion.getExcursionPhotos()){
            path = "Excursions\\" + photo.getPathPhoto();
            InteractionPhoto.deletePhoto(path);
        }

        excursionService.deleteExcursionById(excursionId);
        return "redirect:" + referrer;
    }
}
