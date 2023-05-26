package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.*;
import com.alexdebur.TurMurom.Services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.ServletContextResource;

import javax.activation.FileTypeMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api", produces="application/json")
@CrossOrigin(origins="*")
public class ApiController {
    private MarkService markService;
    private MarkPhotoService markPhotoService;
    private CategoryService categoryService;
    private ExcursionService excursionService;
    private ExcursionPhotoService excursionPhotoService;
    private GuideService guideService;
    private ScheduleService scheduleService;
    private RouteService routeService;

    public static String EXCURSION_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Excursions\\";
    public static String GUIDE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Guides\\";
    public static String MARK_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Marks\\";
    public static String ROUTE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Routes\\";
    public static String USER_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Users\\";

    @Autowired
    private ServletContext servletContext;

    private final UserService userService;

    @Autowired
    public void setMarkService(MarkService markService, CategoryService categoryService, MarkPhotoService markPhotoService,
                               ExcursionService excursionService, ExcursionPhotoService excursionPhotoService,
                               GuideService guideService, RouteService routeService,
                               ScheduleService scheduleService){
        this.markService = markService;
        this.markPhotoService = markPhotoService;
        this.categoryService = categoryService;
        this.excursionService = excursionService;
        this.excursionPhotoService = excursionPhotoService;
        this.guideService = guideService;
        this.routeService = routeService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/admin/user/{id}/changeRole/{role}")
    public String userEdit(@PathVariable Long id, @PathVariable String role) {
        User user = userService.getUserById(id);

        if(role.equals("ROLE_GUIDE")){
            Guide guide = new Guide();
            guide.setLastName(user.getLastName());
            guide.setFirstName(user.getFirstName());
            guide.setPatronymic("");
            guide.setEmail(user.getEmail());
            guide.setTelNumber("");
            guide.setPathPhoto(user.getPathPhoto());

            guideService.insertGuide(guide);
            user.setGuideId(guideService.getGuideByEMail(user.getEmail()).getId());
        }
        if(user.getRoles().contains(Role.ROLE_GUIDE)){
            guideService.deleteGuideById(user.getGuideId());
            user.setGuideId(null);
        }

        userService.changeUserRole(user, role);
        return "true";
    }

    @GetMapping("/auth/login")
    public String login(Principal principal, Model model) {

        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "authorization/log_in";
    }

//    @PostMapping("/login")
//    public User login(@Valid @RequestBody LoginForm form, BindingResult bindingResult,
//                      HttpServletRequest request) {
//        if (bindingResult.hasErrors()) {
//            throw new AppException("Invalid username or password");
//        }
//
//        try {
//            request.login(form.getUsername(), form.getPassword());
//        } catch (ServletException e) {
//            throw new AppException("Invalid username or password");
//        }
//
//        var auth = (Authentication) request.getUserPrincipal();
//        var user = (User) auth.getPrincipal();
//        log.info("User {} logged in.", user.getUsername());
//
//        return new User(user.getId(), user.getNickname());
//    }

    @GetMapping("/marks")
    public List<ObjectNode> getMarks(){
        List<Mark> marks = markService.getAllMarks();

        ObjectMapper mapper = new ObjectMapper();

        List<ObjectNode> result = new ArrayList<>();

        for(Mark mark : marks){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("id", mark.getId());
            objectNode.put("title", mark.getTitle());
            objectNode.put("description", mark.getDescription());
            objectNode.put("categoryId", mark.getCategory().getId());
            objectNode.put("address", mark.getAddress());
            objectNode.put("axisX", mark.getAxisX());
            objectNode.put("axisY", mark.getAxisY());
            objectNode.put("elected", mark.getElected());
            result.add(objectNode);
        }
        return result;
    }

    @GetMapping("/guides")
    public List<Guide> getGuides(){
        return guideService.getAllGuides();
    }

    @GetMapping("/categories")
    public List<Category> getCategories(){
        return categoryService.getAllCategories();
    }

//    @GetMapping("/categories")
//    public User getUser(){
//        return userService.getUserById();
//    }

    @GetMapping("/schedules")
    public List<ObjectNode> getSchedules(){
        List<Schedule> list = scheduleService.getAllSchedules();

        ObjectMapper mapper = new ObjectMapper();

        List<ObjectNode> result = new ArrayList<>();

        for(Schedule schedule : list){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("id", schedule.getId());
            objectNode.put("markId", schedule.getMark().getId());
            objectNode.put("day", schedule.getDay());
            objectNode.put("start", schedule.getStart());
            objectNode.put("end", schedule.getEnd());
            result.add(objectNode);
        }
        return result;
    }

    @GetMapping("/excursions")
    public List<ObjectNode> getExcursions(){
        List<Excursion> list = excursionService.getAllExcursions();

        ObjectMapper mapper = new ObjectMapper();

        List<ObjectNode> result = new ArrayList<>();

        for(Excursion excursion : list){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("id", excursion.getId());
            objectNode.put("name", excursion.getTitle());
            objectNode.put("description", excursion.getDescription());
            objectNode.put("price", excursion.getPrice());
            objectNode.put("duration", excursion.getDuration());
            objectNode.put("guideId", excursion.getGuide().getId());
            result.add(objectNode);
        }
        return result;
    }

    @GetMapping("/excursionsPhotos")
    public List<ObjectNode> getExcursionsPhotos(){
        List<ExcursionPhoto> list = excursionPhotoService.getAllExcursionPhotos();

        ObjectMapper mapper = new ObjectMapper();

        List<ObjectNode> result = new ArrayList<>();

        for(ExcursionPhoto photo : list){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("id", photo.getId());
            objectNode.put("excursionId", photo.getExcursion().getId());
            objectNode.put("pathPhoto", "http://192.168.0.75:8080/api/photo/excursions/" + photo.getId());
            result.add(objectNode);
        }
        return result;
    }

    @GetMapping("/marksPhotos")
    public List<ObjectNode> getMarksPhotos(){
        List<MarkPhoto> list = markPhotoService.getAllMarkPhotos();

        ObjectMapper mapper = new ObjectMapper();

        List<ObjectNode> result = new ArrayList<>();

        for(MarkPhoto photo : list){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("id", photo.getId());
            objectNode.put("markId", photo.getMark().getId());
            objectNode.put("pathPhoto", "http://192.168.0.75:8080/api/photo/marks/" + photo.getId());
            result.add(objectNode);
        }
        return result;
    }

    @GetMapping("/excursionPhoto/download/{id}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable Long id) {
        Resource file = load(excursionPhotoService.getExcursionPhotoById(id).get().getPathPhoto());
        return ResponseEntity.ok()
                .header( HttpHeaders
                        .CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    public Resource load(String filename) {
        try {
            Path file = Paths.get(EXCURSION_UPLOAD_DIRECTORY + filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

//    @RequestMapping(value = "/image/{id}", method = RequestMethod.GET)
//    public void getImageAsByteArray(@PathVariable Long id, HttpServletResponse response) throws IOException {
//
//        String path = EXCURSION_UPLOAD_DIRECTORY + excursionPhotoService.getExcursionPhotoById(id).get().getPathPhoto();
//
//        InputStream in = servletContext.getResourceAsStream(path);
//        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//        IOUtils.copy(in, response.getOutputStream());
//    }


    @GetMapping("/photo/{type}/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id, @PathVariable String type) throws IOException{
        String path = "";
        switch (type){
            case "marks":
                path = MARK_UPLOAD_DIRECTORY + markPhotoService.getMarkPhotoById(id).get().getPathPhoto();
                break;
            case "excursions":
                path = EXCURSION_UPLOAD_DIRECTORY + excursionPhotoService.getExcursionPhotoById(id).get().getPathPhoto();
                break;
            case "guides":
                path = GUIDE_UPLOAD_DIRECTORY + guideService.getGuideById(id).get().getPathPhoto();
                break;
            case "routes":
                path = ROUTE_UPLOAD_DIRECTORY + routeService.getRouteById(id).getPathPhoto();
                break;
            case "user":
                path = USER_UPLOAD_DIRECTORY + userService.getUserById(id).getPathPhoto();
                break;
            default:
                path = "";
        }

        File img = new File(path);
//        File img = new File("src/main/resources/static/test.jpg");
        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap()
                .getContentType(img))).body(Files.readAllBytes(img.toPath()));
    }

//    @RequestMapping(value = "/excursionsPhotos/upload/{id}", method = RequestMethod.GET)
//    public ResponseEntity<byte[]> getImageAsResponseEntity(@PathVariable Long id) throws IOException {
//        ResponseEntity<byte[]> responseEntity;
//
//        String path = excursionPhotoService.getExcursionPhotoById(id).get().getPathPhoto();
//
//        final HttpHeaders headers = new HttpHeaders();
//        final InputStream in = servletContext
//                .getResourceAsStream(EXCURSION_UPLOAD_DIRECTORY + path);
//        byte[] media = IOUtils.toByteArray(in);
//        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
//        responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
//        return responseEntity;
//    }

//    @GetMapping("/catAPI/{id}")
//    public Optional<Category> getCategory(@PathVariable Long id) {
//        Category cat = categoryService.getCategoryById(id).get();
//
//        Category category = new Category();
//        category.setId(cat.getId());
//        category.setTitle(cat.getTitle());
//
//
//        return Optional.of(category);
//    }
}
