package com.alexdebur.TurMurom.Controllers;

import com.alexdebur.TurMurom.Models.*;
import com.alexdebur.TurMurom.Services.MarkService;
import com.alexdebur.TurMurom.Services.RouteService;
import com.alexdebur.TurMurom.Services.UserService;
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
import java.security.Principal;
import java.util.*;

@Controller
public class RouteController {
    private RouteService routeService;
    private MarkService markService;
    private UserService userService;

    public static String ROUTE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Routes\\";
    public static String MARK_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Marks\\";

    @Autowired
    public void setRouteService(RouteService routeService, MarkService markService, UserService userService) {
        this.markService = markService;
        this.userService = userService;
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

    @GetMapping("/routes/{pageNum}")
    public String routesPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                             Model model, @PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("scheme") String scheme) {

        Page<Route> page = routeService.listAll(pageNum, sortField, sortDir);
        List<Route> allRoutes = page.getContent();

        for (var route : allRoutes) {
            try {
                route.setPathPhoto(InteractionPhoto.getPhoto(ROUTE_UPLOAD_DIRECTORY + route.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        model.addAttribute("routes", allRoutes);
        model.addAttribute("activePage", "routes");

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("scheme", scheme);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "routes/routes";
    }

    @GetMapping("/routes/details/{id}")
    public String detailsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("id") Long id, Principal principal) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Route selectedRoute = routeService.getRouteById(id);

        List<String> photos = new ArrayList<>();

        if(principal != null){
            User user = userService.getUserByPrincipal(principal);
            model.addAttribute("user", user);

            if(user.getGuideId() != null){
                ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 3, 6, 9, 12, 15, 18, 21));
                model.addAttribute("arr", arr);
            }
        }

        try {
            photos.add(InteractionPhoto.getPhoto(ROUTE_UPLOAD_DIRECTORY + selectedRoute.getPathPhoto()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(selectedRoute.getRouteMarks().size() != 0) {
            for (var mark : selectedRoute.getRouteMarks()) {
                for (var photo : mark.getMark().getMarkPhotos()) {
                    try {
                        photos.add(InteractionPhoto.getPhoto(MARK_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }

        ArrayList<Long> list = new ArrayList<>();
        ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 2, 4, 6, 8));

        try {
            List<Mark> marks = new ArrayList<>();
            List<RouteMark> mrk = new ArrayList<>(selectedRoute.getRouteMarks().stream().toList());
            mrk.sort(new RouteComparator());

            for (var mark : mrk){
                for (var photo : mark.getMark().getMarkPhotos()){
                    try {
                        photo.setPathPhoto(InteractionPhoto.getPhoto(MARK_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (var routeMarks : mark.getMark().getRouteMarks()) {
                    if (!list.contains(routeMarks.getRoute().getId())) {
                        try {
                            routeMarks.getRoute().setPathPhoto(InteractionPhoto.getPhoto(ROUTE_UPLOAD_DIRECTORY + routeMarks.getRoute().getPathPhoto()));
                            list.add(routeMarks.getRoute().getId());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                marks.add(mark.getMark());
            }


//            for (var mark : routeService.getRouteMarks(id)) {
//                marks.add(mark.getMark());
//            }
            model.addAttribute("marks", marks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("photos", photos);

        model.addAttribute("arr", arr);

        model.addAttribute("route", selectedRoute);


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

        Path fileNameAndPath = Paths.get(ROUTE_UPLOAD_DIRECTORY, fileName);
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

        String photo = ROUTE_UPLOAD_DIRECTORY + routeService.getRouteById(id).getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("photo", photo);

        List<Mark> marks = new ArrayList<>();
        try {
            for (var mark : routeService.getRouteMarks(id)) {
                marks.add(mark.getMark());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Свободные достопримечательности
        List<Mark> freeMarks = new ArrayList<>();
        markService.getAllMarks().forEach(mark -> {
            if(!marks.contains(mark))
                freeMarks.add(mark);
        });
        model.addAttribute("freeMarks", freeMarks);

        fillModelWithRouteAndMarks(model, routeService.getRouteById(id), markService.getAllMarks(),
                marks);
        return "routes/edit";
    }

    @PostMapping("/routes/editRoute")
    public String editRoute(Route route, @RequestParam("image") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            String fileName = "route_" + route.getTitle() + "_" +
                    route.getId() +
                    file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            Path fileNameAndPath = Paths.get(ROUTE_UPLOAD_DIRECTORY, fileName);
            try {
                String path = "Routes\\" + route.getPathPhoto();
                InteractionPhoto.deletePhoto(path);

                Files.write(fileNameAndPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            route.setPathPhoto(fileName);
        }
        routeService.insertRoute(route);
        return "redirect:/routes/1?sortField=title&sortDir=asc&scheme=card";
    }

    @GetMapping("/routes/edit/{routeId}/addMarkToRoute/{markId}")
    public String insertMarkToRoute(
            @PathVariable("markId") Long markId,
            @PathVariable("routeId") Long routeId) throws IOException {

        Route route = routeService.getRouteById(routeId);
        Set<RouteMark> marks = route.getRouteMarks();

        //routeService.getRouteById(routeId).get().setMarks(); markService.getMarkById(markId).get()

        int i = 0;
        for (var item : route.getRouteMarks()) {
            if (i < item.getOrdinal())
                i = item.getOrdinal();
        }

        marks.add(new RouteMark(markService.getMarkById(markId), route, i + 1));
        route.setRouteMarks(marks);
//        route.setMarks(marks);

        routeService.insertRoute(route);

        return "redirect:/routes/edit/" + routeId;
    }

    @GetMapping("/routes/delete/{id}")
    public String deleteRouteById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                  @PathVariable("id") Long id) {
        Route route = routeService.getRouteById(id);

        String path;
        path = "Routes\\" + route.getPathPhoto();
        InteractionPhoto.deletePhoto(path);

        routeService.deleteRouteById(id);
        return "redirect:" + referrer;
    }
}

class RouteComparator implements java.util.Comparator<RouteMark> {
    @Override
    public int compare(RouteMark a, RouteMark b) {
        return a.getOrdinal() - b.getOrdinal();
    }
}
