package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Excursion;
import com.alexdebur.TurMurom.Models.Guide;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.Schedule;
import com.alexdebur.TurMurom.Services.ExcursionService;
import com.alexdebur.TurMurom.Services.GuideService;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
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
import java.util.List;

@Controller
public class GuideController {
    private GuideService guideService;
    private ExcursionService excursionService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Guides\\";

    @Autowired
    public void setServices(GuideService guideService, ExcursionService excursionService) {
        this.guideService = guideService;
        this.excursionService = excursionService;
    }

    private void fillModelWithGuide(Model model, Guide guide) {
        model.addAttribute("guide", guide);
        model.addAttribute("selectedGuide", guide);

    }

    @GetMapping("/guides/{pageNum}")
    public String placesPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                             Model model, @PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("scheme") String scheme) {

        Page<Guide> page = guideService.listAll(pageNum, sortField, sortDir);
        List<Guide> allGuides = page.getContent();

        for (var guide : allGuides){
                try {
                    guide.setPathPhoto(InteractionPhoto.getPhoto(UPLOAD_DIRECTORY + guide.getPathPhoto()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }

        model.addAttribute("guides", allGuides);
        model.addAttribute("activePage", "guides");

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("scheme", scheme);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "guides/guides";
    }

    @GetMapping("/guides/create")
    public String createMark(Model model) {
        fillModelWithGuide(model, new Guide());
        return "guides/insert";
    }

    @GetMapping("/guides/details/{id}")
    public String detailsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("id") Long id) {
        Guide selectedGuide = guideService.getGuideById(id).get();

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        String photo = UPLOAD_DIRECTORY + selectedGuide.getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Excursion> excursions = selectedGuide.getExcursions();
        model.addAttribute("selectedGuide", selectedGuide);
        model.addAttribute("photo", photo);
        model.addAttribute("excursions", excursions);
        return "guides/details";
    }

    @GetMapping("/guides/edit/{id}")
    public String editPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                           Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Guide selectedGuide = guideService.getGuideById(id).get();

        String photo = UPLOAD_DIRECTORY + selectedGuide.getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fillModelWithGuide(model, selectedGuide);

        List<Excursion> excursions = selectedGuide.getExcursions();
        //model.addAttribute("selectedGuide", selectedGuide);
        model.addAttribute("photo", photo);
        model.addAttribute("excursions", excursions);
        return "guides/edit";
    }

    @PostMapping("/guides/addingGuide")
    public String insertGuide(@ModelAttribute Guide guide, @RequestParam("image") MultipartFile file) {

        String fileName = guide.getLastName() + "_" + guide.getTelNumber() +
                file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        guide.setPathPhoto(fileName);
        guideService.insertGuide(guide);
        return "redirect:/guides/1?sortField=lastName&sortDir=asc&scheme=list";
    }

//    @PostMapping("/uploadImage")
//    //@ResponseBody
//    public String uploadImage(@ModelAttribute Guide guide, Model model, @RequestParam MultipartFile upload) throws IOException {
//        StringBuilder fileNames = new StringBuilder();
//        String fileName = guide.getId() + upload.getOriginalFilename().substring(upload.getOriginalFilename().length() - 4);
//        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
//        fileNames.append(fileName);
//        Files.write(fileNameAndPath, upload.getBytes());
//        model.addAttribute("photo", fileName);
//        return "Successful";
//    }


    @PostMapping("/guides/editGuide")
    public String editGuide(Guide guide, @RequestParam("image") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            String fileName = guide.getLastName() + "_" + guide.getTelNumber() +
                    file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            guide.setPathPhoto(fileName);
        }
        guideService.insertGuide(guide);
        return "redirect:/guides/1?sortField=lastName&sortDir=asc&scheme=list";
    }

    @GetMapping("/guides/delete/{id}")
    public String deleteGuideById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                  @PathVariable("id") Long id) {
        Guide guide = guideService.getGuideById(id).get();

        String path;
        path = "Guides\\" + guide.getPathPhoto();
        InteractionPhoto.deletePhoto(path);

        guideService.deleteGuideById(id);
        return "redirect:" + referrer;
    }
}
