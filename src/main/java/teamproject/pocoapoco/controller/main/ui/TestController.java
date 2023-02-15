package teamproject.pocoapoco.controller.main.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

@Controller
@RequestMapping("/view/v1")
@RequiredArgsConstructor
@Transactional
public class TestController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/crews/write")
    public String hello(Model model){
        List<SportEnum> sportEnums = List.of(SportEnum.values());
        model.addAttribute("sportEnums",sportEnums);
        return "crew/write";
    }


}
