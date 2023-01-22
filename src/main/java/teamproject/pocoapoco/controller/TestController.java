package teamproject.pocoapoco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/v1")
public class TestController {
    @GetMapping("/start")
    public String hello(){
        return "index";
    }
}
