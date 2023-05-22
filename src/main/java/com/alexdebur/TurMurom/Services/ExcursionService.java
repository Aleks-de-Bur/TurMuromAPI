package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.Excursion;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Repositories.ExcursionRepository;
import com.alexdebur.TurMurom.Repositories.GuideRepository;
import com.alexdebur.TurMurom.Repositories.UserElectedExcursionRepository;
import com.alexdebur.TurMurom.Repositories.UserElectedMarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExcursionService {
    private ExcursionRepository excursionRepository;
    private GuideRepository guideRepository;
    private UserElectedExcursionRepository userElectedExcursionRepository;

    @Autowired
    public void setRepositories(ExcursionRepository excursionRepository,
                                       GuideRepository guideRepository,
                                UserElectedExcursionRepository userElectedExcursionRepository) {
        this.excursionRepository = excursionRepository;
        this.guideRepository = guideRepository;
        this.userElectedExcursionRepository = userElectedExcursionRepository;
    }

    public List<Excursion> getAllExcursions() {
        return excursionRepository.findAll();
    }

    public List<Excursion> getElectedExcursions(Long id) {
        List<Excursion> excursions = new ArrayList<>();

        for (var userExcursion : userElectedExcursionRepository.findByUserId(id)){
            try {
                excursions.add(userExcursion.getExcursion());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return excursions;
    }

    public Excursion getExcursionById(Long id) {
        return excursionRepository.findById(id).get();
    }

    public void insertExcursion(Excursion excursion) {
        excursionRepository.save(excursion);
    }

    public void deleteExcursionById(Long id) {
        excursionRepository.deleteById(id);
    }

    /*
     * TODO: Get Excursion By keyword and Pagination
     */
    public Page<Excursion> listAll(int pageNum, int size, String keyword, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        Page<Excursion> pageExcursions;
        if (keyword == null) {
            pageExcursions = excursionRepository.findAll(pageable);
        } else {
            pageExcursions = excursionRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        }

        return pageExcursions;
    }
}
