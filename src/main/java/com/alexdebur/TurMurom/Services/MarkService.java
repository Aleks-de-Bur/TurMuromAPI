package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.MarkPhoto;
import com.alexdebur.TurMurom.Repositories.MarkRepository;
import com.alexdebur.TurMurom.Repositories.UserElectedMarkRepository;
import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarkService {
    private MarkRepository markRepository;
    private UserElectedMarkRepository userElectedMarkRepository;

    @Autowired
    public void setMarkRepository(MarkRepository markRepository, UserElectedMarkRepository userElectedMarkRepository) {
        this.markRepository = markRepository;
        this.userElectedMarkRepository = userElectedMarkRepository;
    }

    public List<Mark> getAllMarks() {
        return markRepository.findAll();
    }

    public List<Mark> getElectedMarks(Long id) {
        List<Mark> marks = new ArrayList<>();

        for (var userMark : userElectedMarkRepository.findByUserId(id)){
            try {
                marks.add(userMark.getMark());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return marks;
    }

    public Mark getMarkById(Long id) {
        return markRepository.findById(id).get();
    }

    public Optional<Mark> getMarkByTitleAndDescription(String title, String description) {
        return markRepository.findByTitleAndDescription(title, description);
    }

    public void deleteElectedMark(Long id){
        userElectedMarkRepository.deleteById(id);
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

    /*
     * TODO: Get Mark By keyword and Pagination
     */
    public Page<Mark> listAll(int pageNum, int size, String filter, String keyword, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        Page<Mark> pageMarks;
        if (keyword == null && filter.equals("Все")) {
            pageMarks = markRepository.findAll(pageable);
        } else if(keyword != null && filter.equals("Все")){
            pageMarks = markRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else if(keyword == null){
            pageMarks = markRepository.findByCategoryTitleContainingIgnoreCase(filter, pageable);
        } else
            pageMarks = markRepository.findByTitleContainingIgnoreCaseAndCategoryTitleContainingIgnoreCase(keyword, filter, pageable);

        return pageMarks;
    }
}
