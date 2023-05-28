package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.MarkPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface MarkPhotoRepository extends JpaRepository<MarkPhoto, Long> {

}
