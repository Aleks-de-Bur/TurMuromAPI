package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.Guide;
import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.Route;
import com.alexdebur.TurMurom.Repositories.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GuideService {
    private GuideRepository guideRepository;

    @Autowired
    public void setRepository(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    public List<Guide> getAllGuides() {
        return guideRepository.findAll();
    }

    public Optional<Guide> getGuideById(Long id) {
        return guideRepository.findById(id);
    }

    public void insertGuide(Guide guide) {
        guideRepository.save(guide);
    }

    public void deleteGuideById(Long id) {
        guideRepository.deleteById(id);
    }

    public Page<Guide> listAll(int pageNum, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, 7,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );
        return guideRepository.findAll(pageable);
    }
}
