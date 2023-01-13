package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.*;
import com.alexdebur.TurMurom.Services.MarkService;
import com.alexdebur.TurMurom.Services.RouteService;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Controller
public class RouteController {
    private RouteService routeService;
    private MarkService markService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Routes\\";

    @Autowired
    public void setRouteService(RouteService routeService, MarkService markService) {
        this.markService = markService;
        this.routeService = routeService;
    }

    private void fillModelWithRoute(Model model, Route route) {
        model.addAttribute("route", route);
    }

    private void fillModelWithRouteAndMarks(Model model, Route route, List<Mark> allMarks, List<Mark> marks) {
        model.addAttribute("allMarks", allMarks);
        model.addAttribute("marks", marks);
        model.addAttribute("route", route);
    }

    @GetMapping("/routes")
    public String routesPage(Model model) {
        List<Route> allRoutes = routeService.getAllRoutes();
        model.addAttribute("routes", allRoutes);
        model.addAttribute("activePage", "routes");
        return "routes/routes";
    }

    @GetMapping("/routes/details/{id}")
    public String detailsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Route selectedRoute = routeService.getRouteById(id).get();

        String photo = UPLOAD_DIRECTORY + selectedRoute.getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("photo", photo);
        model.addAttribute("selectedRoute", selectedRoute);

        model.addAttribute("marks", routeService.getRouteById(id).get().getMarks());
        return "routes/details";
    }

    @GetMapping("/routes/create")
    public String createRoute(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        fillModelWithRoute(model, new Route());
        return "routes/insert";
    }

    @PostMapping("/routes/addingRoute")
    public String insertRoute(Route route, @RequestParam("image") MultipartFile file) throws IOException {

        String fileName = "route_" + route.getTitle() + "_" +
                (routeService.getAllRoutes().size() + 1) +
                file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        route.setPathPhoto(fileName);
        routeService.insertRoute(route);

        Long id = routeService.getAllRoutes().get(routeService.getAllRoutes().lastIndexOf(route)).getId();

        return "redirect:/routes/edit/" + id;
    }

    @GetMapping("/routes/edit/{id}")
    public String editRoute(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                            Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        String photo = UPLOAD_DIRECTORY + routeService.getRouteById(id).get().getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("photo", photo);

        fillModelWithRouteAndMarks(model, routeService.getRouteById(id).get(), markService.getAllMarks(),
                routeService.getRouteById(id).get().getMarks());
        return "routes/edit";
    }

    @PostMapping("/routes/editRoute")
    public String editRoute(Route route, @RequestParam("image") MultipartFile file) throws IOException {

        if (file.getOriginalFilename() != "") {
            String fileName = "route_" + route.getTitle() + "_" +
                    route.getId() +
                    file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            route.setPathPhoto(fileName);
        }
        routeService.insertRoute(route);
        return "redirect:/routes";
    }

    @PostMapping("/routes/edit/{routeId}/addMarkToRoute/{markId}")
    public String insertMarkToRoute(
            @PathVariable("markId") Long markId,
            @PathVariable("routeId") Long routeId) throws IOException {

        Route route = routeService.getRouteById(routeId).get();
        List<Mark> marks = new ArrayList<>();
        try {
            marks = route.getMarks();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //routeService.getRouteById(routeId).get().setMarks(); markService.getMarkById(markId).get()

        marks.add(markService.getMarkById(markId).get());
        route.setMarks(marks);

        routeService.insertRoute(route);

        return "redirect:/routes/edit/" + routeId;
    }

    @GetMapping("/routes/delete/{id}")
    public String deleteRouteById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                  @PathVariable("id") Long id) {
        Route route = routeService.getRouteById(id).get();

        String path;
        path = "Routes\\" + route.getPathPhoto();
        InteractionPhoto.deletePhoto(path);

        routeService.deleteRouteById(id);
        return "redirect:" + referrer;
    }
}
