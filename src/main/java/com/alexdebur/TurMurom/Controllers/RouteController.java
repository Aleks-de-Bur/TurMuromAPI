package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.Route;
import com.alexdebur.TurMurom.Services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.util.List;

@Controller
public class RouteController {
    private RouteService routeService;

    @Autowired
    public void setRouteService(RouteService routeService){
        this.routeService = routeService;
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
        model.addAttribute("selectedRoute", selectedRoute);
        return "routes/details";
    }

    @GetMapping("/routes/delete/{id}")
    public String deleteRouteById(@PathVariable("id") Long id){
        routeService.deleteRouteById(id);
        return "redirect:/routes/routes";
    }
}
