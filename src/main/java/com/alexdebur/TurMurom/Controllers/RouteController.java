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
import java.util.List;

import static com.alexdebur.TurMurom.Controllers.GuideController.uploadDirectory;

@Controller
public class RouteController {
    private RouteService routeService;
    private MarkService markService;

    @Autowired
    public void setRouteService(RouteService routeService, MarkService markService){
        this.markService = markService;
    }

    private void fillModelWithRoute(Model model, Route route){
        model.addAttribute("route", route);
    }

    @GetMapping("/routes")
    public String placesPage(Model model) {
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

    @PostMapping("/places/addingRoute")
    public String insertRoute(Route route, @RequestParam("image")MultipartFile file) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        String fileName = "route_" + route.getTitle() + "_" +
                (routeService.getAllRoutes().size() + 1) +
                file.getOriginalFilename().substring(file.getOriginalFilename().length()-4);

        String path = System.getProperty("user.dir")+"/RoutePhotos/";
        Path fileNameAndPath = Paths.get(path, fileName);

        fileNames.append(file.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        route.setPathPhoto(fileName);



        routeService.insertRoute(route);

        return "redirect:/routes/routes";
    }

    @GetMapping("/routes/delete/{id}")
    public String deleteRouteById(@PathVariable("id") Long id){
        routeService.deleteRouteById(id);
        return "redirect:/routes/routes";
    }
}
