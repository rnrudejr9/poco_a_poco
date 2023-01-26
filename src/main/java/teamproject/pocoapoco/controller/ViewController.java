package teamproject.pocoapoco.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import teamproject.pocoapoco.domain.dto.user.UserJoinRequest;
import teamproject.pocoapoco.domain.dto.user.UserLoginRequest;
import teamproject.pocoapoco.service.UserService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ViewController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(UserJoinRequest userJoinRequest){

        userService.saveUser(userJoinRequest);
        return "test/test";
    }
    @PostMapping("/signin")
    public String login(UserLoginRequest userLoginRequest){

        userService.login(userLoginRequest);
        return "test/test";
    }
    @GetMapping("/start")
    public String testForm(Model model) {
        model.addAttribute("userLoginRequest",new UserLoginRequest());
        model.addAttribute("userJoinRequest",new UserJoinRequest());
        return "start/start";
    }
}
