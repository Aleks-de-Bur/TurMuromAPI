package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.MarkPhoto;
import com.alexdebur.TurMurom.Repositories.MarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarkService {
    private MarkRepository markRepository;

    @Autowired
    public void setMarkRepository(MarkRepository markRepository) {
        this.markRepository = markRepository;
    }

    public List<Mark> getAllMarks() {
        return markRepository.findAll();
    }

    public Optional<Mark> getMarkById(Long id) {
        return markRepository.findById(id);
    }

    public Optional<Mark> getMarkByTitleAndDescription(String title, String description) {
        return markRepository.findByTitleAndDescription(title, description);
    }

    public void insertMark(Mark mark) {
        markRepository.save(mark);
    }

    public void deleteMarkById(Long id) {
        markRepository.deleteById(id);
    }

    public List<File> getPhotos(Mark mark) {
        var markPhotos = mark.getMarkPhotos();
        if (markPhotos.size() == 0){
            return new ArrayList<>();
        }
        else{
            String path = markPhotos.get(0).getPathPhoto();
            File photo = new File(path);
            List<File> photos = new ArrayList<>();
            photos.add(photo);
            return photos;
        }
    }






    // Pagination
    public Page<Mark> listAll(int pageNum, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, 5,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        return markRepository.findAll(pageable);
    }
}
