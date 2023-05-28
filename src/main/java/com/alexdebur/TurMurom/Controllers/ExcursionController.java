package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.*;
import com.alexdebur.TurMurom.Repositories.MarkRepository;
import com.alexdebur.TurMurom.Repositories.UserElectedExcursionRepository;
import com.alexdebur.TurMurom.Repositories.UserElectedMarkRepository;
import com.alexdebur.TurMurom.Services.ExcursionPhotoService;
import com.alexdebur.TurMurom.Services.ExcursionService;
import com.alexdebur.TurMurom.Services.GuideService;
import com.alexdebur.TurMurom.Services.UserService;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
public class ExcursionController {
    private ExcursionService excursionService;
    private ExcursionPhotoService excursionPhotoService;
    private GuideService guideService;
    private UserService userService;

    public static String EXCURSION_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Excursions\\";
    //public static String GUIDE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Guides\\";
    public static String USER_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Users\\";

    private final UserElectedExcursionRepository userElectedExcursionRepository;

    public ExcursionController(UserElectedExcursionRepository userElectedExcursionRepository) {
        this.userElectedExcursionRepository = userElectedExcursionRepository;
    }

    @Autowired
    public void setExcursionService(ExcursionService excursionService, ExcursionPhotoService excursionPhotoService,
                                    GuideService guideService, UserService userService) {
        this.excursionService = excursionService;
        this.excursionPhotoService = excursionPhotoService;
        this.guideService = guideService;
        this.userService = userService;
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
                                 @RequestParam(defaultValue = "card") String scheme,
                                 Principal principal) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        if(principal != null){
            User user = userService.getUserByPrincipal(principal);
            model.addAttribute("user", user);

            model.addAttribute("guide", "");

            if(user.getGuideId() != null){
                Guide guide = guideService.getGuideById(user.getGuideId()).get();
                model.addAttribute("guide", guide);
                ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 3, 6, 9, 12, 15, 18, 21));
                model.addAttribute("arr", arr);
            }


        } else model.addAttribute("guide", "");

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
                        photo.setPathPhoto(InteractionPhoto.getPhoto(EXCURSION_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (!list.contains(excursion.getGuide().getId())) {
                    try {
                        excursion.getGuide().setPathPhoto(InteractionPhoto
                                .getPhoto(USER_UPLOAD_DIRECTORY + excursion.getGuide().getPathPhoto()));
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
        return "excursions/excursions";
    }

//    @RequestMapping("/excursion/elect")
//    public ResponseEntity<?> electedExcursion(Long excursionId, Long userId) throws IOException {
//        User user = userService.getUserById(userId);
//        Set<UserElectedExcursion> elected = user.getUserElectedExcursions();
//
//        UserElectedExcursion check = new UserElectedExcursion(user, excursionService.getExcursionById(excursionId));
//
//        if (elected.contains(check)){
//            elected.remove(check);
//        } else {
//            elected.add(check);
//        }
//        user.setUserElectedExcursions(elected);
//        userService.editUser(user);
//
//        return ResponseEntity.ok(check);
//    }

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

        Excursion selectedExcursion = excursionService.getExcursionById(excursionId);
        List<String> photos = new ArrayList<>();
        for (var photo : selectedExcursion.getExcursionPhotos()){
            try {
                photos.add(InteractionPhoto.getPhoto(EXCURSION_UPLOAD_DIRECTORY + photo.getPathPhoto()));
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

        List<String> excursionPhotos = new ArrayList<>();

        for (var photo : excursionService.getExcursionById(excursionId).getExcursionPhotos()) {
            try {
                excursionPhotos.add(InteractionPhoto.getPhoto(EXCURSION_UPLOAD_DIRECTORY + photo.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        List<ExcursionPhoto> photos = excursionService.getExcursionById(excursionId).getExcursionPhotos();

        model.addAttribute("excursionPhotos", excursionPhotos);

        fillModelWithExcursion(model, excursionService.getExcursionById(excursionId), photos);
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

        Excursion excursion = excursionService.getExcursionById(excursionId);

        String path;
        for (var photo: excursion.getExcursionPhotos()){
            path = "Excursions\\" + photo.getPathPhoto();
            InteractionPhoto.deletePhoto(path);
        }

        excursionService.deleteExcursionById(excursionId);
        return "redirect:" + referrer;
    }


    @PostMapping("/excursion/{excursionId}/elect/{locate}")
    public String electExcursion(Excursion excursion, Principal principal, @PathVariable("locate") String locate,
                                @PathVariable("excursionId") Long excursionId) throws IOException {
        User user = userService.getUserByPrincipal(principal);
        Set<UserElectedExcursion> elected = user.getUserElectedExcursions();

        UserElectedExcursion check = new UserElectedExcursion(user, excursionService.getExcursionById(excursionId));

        if (elected.contains(check)){
            check = userElectedExcursionRepository.findByUserIdAndExcursionId(user.getId(), excursionId);
            elected.remove(check);
            userElectedExcursionRepository.deleteById(check.getId());
        } else {
            elected.add(check);
        }
        user.setUserElectedExcursions(elected);
        userService.editUser(user);

        if(locate.equals("excursions"))
            return "redirect:/excursions";
        else
            return "redirect:/profile/elected";
    }
}
