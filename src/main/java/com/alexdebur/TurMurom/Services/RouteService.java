package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.Excursion;
import com.alexdebur.TurMurom.Models.Route;
import com.alexdebur.TurMurom.Models.RouteMark;
import com.alexdebur.TurMurom.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    private RouteRepository routeRepository;
    private MarkRepository markRepository;
    private RouteMarkRepository routeMarkRepository;

    @Autowired
    public void setRepositories(RouteRepository routeRepository,
                                MarkRepository markRepository, RouteMarkRepository routeMarkRepository) {
        this.routeRepository = routeRepository;
        this.markRepository = markRepository;
        this.routeMarkRepository = routeMarkRepository;
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id).get();
    }

    public List<RouteMark> getRouteMarks(Long id) {
        return routeMarkRepository.findByRouteId(id);
    }

    public void insertRoute(Route route) {
        routeRepository.save(route);
    }
    public void insertRouteMark(RouteMark routeMark) {
        routeMarkRepository.save(routeMark);
    }

    public void deleteRouteById(Long id) {
        routeRepository.deleteById(id);
    }
    public void deleteRouteMarkById(Long id) {
        routeMarkRepository.deleteM(id);
    }
    public void deleteRouteMark(RouteMark routeMark) {
        routeMarkRepository.delete(routeMark);
    }

    public Page<Route> listAll(int pageNum, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, 7,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );
        return routeRepository.findAll(pageable);
    }
}
