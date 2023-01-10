package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.*;
import com.alexdebur.TurMurom.Services.ExcursionPhotoService;
import com.alexdebur.TurMurom.Services.ExcursionService;
import com.alexdebur.TurMurom.Services.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Controller
public class ExcursionController {
    private ExcursionService excursionService;
    private ExcursionPhotoService excursionPhotoService;
    private GuideService guideService;

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
    public String excursionsPage(Model model) {
        List<Excursion> allExcursions = excursionService.getAllExcursions();
        model.addAttribute("excursions", allExcursions);
        model.addAttribute("activePage", "excursions");
        return "excursions/excursions";
    }

    @GetMapping("/guides/edit/{guideId}/excursion/create")
    public String createExcursion(Model model, @PathVariable("guideId") Long guideId) {
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
    public String detailsPage(Model model, @PathVariable("excursionId") Long excursionId) {
        Excursion selectedExcursion = excursionService.getExcursionById(excursionId).get();
        model.addAttribute("selectedExcursion", selectedExcursion);
        return "excursions/details";
    }

    @GetMapping("/guides/edit/{guideId}/excursion/{excursionId}/edit")
    public String editExcursion(Model model, @PathVariable("guideId") Long guideId,
                                @PathVariable("excursionId") Long excursionId) {
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
    public String deleteExcursionById(@PathVariable("excursionId") Long excursionId,
                                      @PathVariable("guideId") Long guideId) {
        excursionService.deleteExcursionById(excursionId);
        return "redirect:/guides/details/" + guideId;
    }
}
