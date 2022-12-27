package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.MarkPhoto;
import com.alexdebur.TurMurom.Repositories.MarkPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarkPhotoService {
    private MarkPhotoRepository markPhotoRepository;

    @Autowired
    public void setMarkPhotoRepository(MarkPhotoRepository markPhotoRepository) {
        this.markPhotoRepository = markPhotoRepository;
    }

    public List<MarkPhoto> getAllMarkPhotos() {
        return markPhotoRepository.findAll();
    }

    public Optional<MarkPhoto> getMarkPhotoById(Long id) {
        return markPhotoRepository.findById(id);
    }

    public void insertMarkPhoto(MarkPhoto markPhoto) {
        markPhotoRepository.save(markPhoto);
    }

    public void deleteMarkPhotoById(Long id) {
        markPhotoRepository.deleteById(id);
    }
}
