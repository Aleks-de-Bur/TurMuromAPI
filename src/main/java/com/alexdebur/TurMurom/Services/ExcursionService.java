package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.Excursion;
import com.alexdebur.TurMurom.Repositories.ExcursionRepository;
import com.alexdebur.TurMurom.Repositories.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExcursionService {
    private ExcursionRepository excursionRepository;
    private GuideRepository guideRepository;

    @Autowired
    public void setRepositories(ExcursionRepository excursionRepository,
                                       GuideRepository guideRepository) {
        this.excursionRepository = excursionRepository;
        this.guideRepository = guideRepository;
    }

    public List<Excursion> getAllExcursions() {
        return excursionRepository.findAll();
    }

    public Optional<Excursion> getExcursionById(Long id) {
        return excursionRepository.findById(id);
    }

    public void insertExcursion(Excursion excursion) {
        excursionRepository.save(excursion);
    }

    public void deleteExcursionById(Long id) {
        excursionRepository.deleteById(id);
    }
}
