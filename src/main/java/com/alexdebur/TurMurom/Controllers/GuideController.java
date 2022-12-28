package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Excursion;
import com.alexdebur.TurMurom.Models.Guide;
import com.alexdebur.TurMurom.Models.Schedule;
import com.alexdebur.TurMurom.Services.ExcursionService;
import com.alexdebur.TurMurom.Services.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public static String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/uploads/photos/guides";

    @Autowired
    public void setServices(GuideService guideService, ExcursionService excursionService){
        this.guideService = guideService;
        this.excursionService = excursionService;
    }

    private void fillModelWithGuide(Model model, Guide guide){
        model.addAttribute("guide", guide);

    }

    @GetMapping("/guides")
    public String placesPage(Model model) {
        List<Guide> allGuides = guideService.getAllGuides();
        model.addAttribute("guides", allGuides);
        model.addAttribute("activePage", "places");
        return "guides/guides";
    }

    @GetMapping("/guides/create")
    public String createMark(Model model) {
        fillModelWithGuide(model, new Guide());
        return "guides/insert";
    }

    @GetMapping("/guides/details/{id}")
    public String detailsPage(Model model, @PathVariable("id") Long id) {
        Guide selectedGuide = guideService.getGuideById(id).get();
        String photo = "/photos/guides/" + selectedGuide.getPathPhoto(); //+ ".jpg";
        //String photo = "/uploads/photos/guides/" + selectedGuide.getPathPhoto(); //+ ".jpg";
        List<Excursion> excursions = selectedGuide.getExcursions();
        model.addAttribute("selectedGuide", selectedGuide);
        model.addAttribute("photo", photo);
        model.addAttribute("excursions", excursions);
        return "guides/details";
    }

    @GetMapping("/guides/edit/{id}")
    public String editPage(Model model, @PathVariable("id") Long id) {
        Guide selectedGuide = guideService.getGuideById(id).get();
        String photo = "/photos/guides/" + selectedGuide.getPathPhoto() + ".jpg";
        List<Excursion> excursions = selectedGuide.getExcursions();
        model.addAttribute("selectedGuide", selectedGuide);
        model.addAttribute("photo", photo);
        model.addAttribute("excursions", excursions);
        return "guides/edit";
    }

    @PostMapping("/guides/addingGuide")
    public String insertGuide(@ModelAttribute Guide guide, @RequestParam("image")MultipartFile file){
        StringBuilder fileNames = new StringBuilder();
        String fileName = "guide_" + guide.getLastName() + "_" +
                (guideService.getAllGuides().size() + 1) +
                file.getOriginalFilename().substring(file.getOriginalFilename().length()-4);
        Path fileNameAndPath = Paths.get(uploadDirectory, fileName);
        fileNames.append(file.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        guide.setPathPhoto(fileName);
        //guide.setPathPhoto("/photos/guides/" + "guide_lakin" + ".jpg");
        guideService.insertGuide(guide);
        return "redirect:/guides";
    }

    @PostMapping("/uploadImage")
    //@ResponseBody
    public String uploadImage(@ModelAttribute Guide guide, Model model, @RequestParam("image") MultipartFile file) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        String fileName = guide.getId() + file.getOriginalFilename().substring(file.getOriginalFilename().length()-4);
        Path fileNameAndPath = Paths.get(uploadDirectory, fileName);
        fileNames.append(fileName);
        Files.write(fileNameAndPath, file.getBytes());
        model.addAttribute("photo", fileName);
        return "Successful";
    }


    @PutMapping("/guides/editGuide")
    public String updateMark(@ModelAttribute Guide guide){
        guideService.insertGuide(guide);
        return "redirect:/guides";
    }

    @GetMapping("/guides/delete/{id}")
    public String deleteGuideById(@PathVariable("id") Long id){
        guideService.deleteGuideById(id);
        return "redirect:/guides";
    }
}
