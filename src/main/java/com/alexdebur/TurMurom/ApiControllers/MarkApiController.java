package com.alexdebur.TurMurom.ApiControllers;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Services.MarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MarkApiController {
    private MarkService markService;

    @Autowired
    public void setMarkService(MarkService markService){
        this.markService = markService;
    }

    @GetMapping("/index")
    public List<Mark> mainForm(){
        return markService.getAllMarks();
    }
}
