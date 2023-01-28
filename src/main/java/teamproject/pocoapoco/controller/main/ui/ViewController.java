package teamproject.pocoapoco.controller.main.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import teamproject.pocoapoco.domain.dto.user.UserJoinRequest;
import teamproject.pocoapoco.domain.dto.user.UserLoginRequest;
import teamproject.pocoapoco.domain.dto.user.UserLoginResponse;
import teamproject.pocoapoco.service.UserService;

import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ViewController {

    private final UserService userService;

    @PostMapping("/view/v1/signup")
    public String signup(UserJoinRequest userJoinRequest){

        userService.saveUser(userJoinRequest);
        return "redirect:/view/v1/start";
    }
    @PostMapping("/view/v1/signin")
    public String login(UserLoginRequest userLoginRequest){

        userService.login(userLoginRequest);
        return "redirect:/view/v1/crews";
    }
    @GetMapping("/view/v1/start")
    public String testForm(Model model) {
        model.addAttribute("userLoginRequest",new UserLoginRequest());
        model.addAttribute("userJoinRequest",new UserJoinRequest());
        return "start/start";
    }
}
