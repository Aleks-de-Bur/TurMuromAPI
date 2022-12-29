package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.*;
import com.alexdebur.TurMurom.Services.MarkService;
import com.alexdebur.TurMurom.Services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.alexdebur.TurMurom.Controllers.GuideController.uploadDirectory;

@Controller
public class RouteController {
    private RouteService routeService;
    private MarkService markService;

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
        return "routes/routes";
    }

    @GetMapping("/routes/details/{id}")
    public String detailsPage(Model model, @PathVariable("id") Long id) {
        Route selectedRoute = routeService.getRouteById(id).get();
        String photo = "/photos/routes/" + selectedRoute.getPathPhoto(); //+ ".jpg";
        //String photo = "/uploads/photos/guides/" + selectedGuide.getPathPhoto(); //+ ".jpg";
//        List<Excursion> excursions = selectedGuide.getExcursions();
        model.addAttribute("photo", photo);
        model.addAttribute("selectedRoute", selectedRoute);
        return "routes/details";
    }

    @GetMapping("/routes/create")
    public String createRoute(Model model) {
        fillModelWithRoute(model, new Route());
        return "routes/insert";
    }

    @PostMapping("/routes/addingRoute")
    public String insertRoute(Route route, @RequestParam("image") MultipartFile file) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        String fileName = "route_" + route.getTitle() + "_" +
                (routeService.getAllRoutes().size() + 1) +
                file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

        String path = System.getProperty("user.dir") + "/Photos/Routes/";
        Path fileNameAndPath = Paths.get(path, fileName);

        fileNames.append(file.getOriginalFilename());
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
    public String editRoute(Model model, @PathVariable("id") Long id) {
        fillModelWithRouteAndMarks(model, routeService.getRouteById(id).get(), markService.getAllMarks(),
                routeService.getRouteById(id).get().getMarks());
        return "routes/edit";
    }

    @PostMapping("/routes/editRoute")
    public String editRoute(Route route, @RequestParam("image") MultipartFile file) throws IOException {


        if (file.getOriginalFilename() != "") {
            StringBuilder fileNames = new StringBuilder();
            String fileName = "route_" + route.getTitle() + "_" +
                    route.getId() +
                    file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            String path = System.getProperty("user.dir") + "/Photos/Routes";
            Path fileNameAndPath = Paths.get(path, fileName);
            fileNames.append(file.getOriginalFilename());
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

//    @GetMapping("/routes/edit/{id}")
//    public String editRoute(Model model, @PathVariable("id") Long id) {
//        //List<Schedule> schedules = scheduleService.getSchedulesByMark(markService.getMarkById(id).get());
//
//        fillModelWithRoute(model, routeService.getRouteById(id).get());
//        return "routes/edit";
//    }

    @GetMapping("/routes/delete/{id}")
    public String deleteRouteById(@PathVariable("id") Long id) {
        routeService.deleteRouteById(id);
        return "redirect:/routes";
    }
}
