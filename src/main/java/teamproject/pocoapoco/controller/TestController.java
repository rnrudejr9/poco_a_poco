package teamproject.pocoapoco.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.service.CrewService;

@Controller
@RequestMapping("/view/v1")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/start")
    public String hello(){
        return "index";
    }

}
