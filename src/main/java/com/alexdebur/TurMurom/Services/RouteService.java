package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.Excursion;
import com.alexdebur.TurMurom.Models.Route;
import com.alexdebur.TurMurom.Repositories.ExcursionRepository;
import com.alexdebur.TurMurom.Repositories.GuideRepository;
import com.alexdebur.TurMurom.Repositories.MarkRepository;
import com.alexdebur.TurMurom.Repositories.RouteRepository;
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

    @Autowired
    public void setRepositories(RouteRepository routeRepository,
                                MarkRepository markRepository) {
        this.routeRepository = routeRepository;
        this.markRepository = markRepository;
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    public void insertRoute(Route route) {
        routeRepository.save(route);
    }

    public void deleteRouteById(Long id) {
        routeRepository.deleteById(id);
    }

    public Page<Route> listAll(int pageNum, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, 7,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );
        return routeRepository.findAll(pageable);
    }
}
