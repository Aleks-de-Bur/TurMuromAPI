package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.ExcursionPhoto;
import com.alexdebur.TurMurom.Repositories.ExcursionPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExcursionPhotoService {
    private ExcursionPhotoRepository excursionPhotoRepository;

    @Autowired
    public void setExcursionPhotoRepository(ExcursionPhotoRepository excursionPhotoRepository) {
        this.excursionPhotoRepository = excursionPhotoRepository;
    }

    public List<ExcursionPhoto> getAllExcursionPhotos() {
        return excursionPhotoRepository.findAll();
    }

    public Optional<ExcursionPhoto> getExcursionPhotoById(Long id) {
        return excursionPhotoRepository.findById(id);
    }

    public void insertExcursionPhoto(ExcursionPhoto markPhoto) {
        excursionPhotoRepository.save(markPhoto);
    }

    public void deleteExcursionPhotoById(Long id) {
        excursionPhotoRepository.deleteById(id);
    }
}
