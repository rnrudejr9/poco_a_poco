package teamproject.pocoapoco.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class ViewController {

    @RequestMapping("/")
    public String test() {

        return "sign/sign";
    }
}
