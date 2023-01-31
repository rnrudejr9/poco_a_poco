package teamproject.pocoapoco.controller.main.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.mail.UserMailResponse;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.dto.user.UserJoinRequest;
import teamproject.pocoapoco.domain.dto.user.UserLoginRequest;
import teamproject.pocoapoco.domain.dto.user.UserLoginResponse;
import teamproject.pocoapoco.service.MailService;
import teamproject.pocoapoco.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ViewController {

    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/view/v1/signup")
    public String signup(UserJoinRequest userJoinRequest){

        userService.saveUser(userJoinRequest);
        return "redirect:/view/v1/start";
    }

    @PostMapping("/view/v1/signin")
    public String login(UserLoginRequest userLoginRequest, HttpServletResponse response) throws UnsupportedEncodingException {

        UserLoginResponse tokens = userService.login(userLoginRequest);

        //cookie 설정은 스페이스가 안되기 때문에 Bearer 앞에 +를 붙인다. Security Filter에서 + -> " " 로 치환할 것이다.
        Cookie cookie = new Cookie("jwt", "Bearer+"+tokens.getAccessToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "redirect:/view/v1/crews";
    }
    @GetMapping("/view/v1/start")
    public String testForm(Model model) {
        model.addAttribute("userLoginRequest",new UserLoginRequest());
        model.addAttribute("userJoinRequest",new UserJoinRequest());
        return "start/start";
    }
    @GetMapping("/view/v1/test")
    public String test(Model model) {
        return "test/test";
    }

    @GetMapping("/view/v1/logout")
    public String logout(HttpServletResponse response) {
        // 시큐리티 세션 파기하지 않았는데도 로그아웃된 이유는 Security Context에 익명유저로 등록이 되었기 때문?
        Cookie cookie = new Cookie("jwt", null);
        response.addCookie(cookie);
        return "redirect:/view/v1/crews";
    }

    @PostMapping("/login/mailConfirm")
    @ResponseBody
    public Response mailConfirm(@RequestParam("email") String email) throws Exception {

        UserMailResponse userMailResponse = mailService.sendSimpleMessage(email);
        System.out.println("인증코드 : " + userMailResponse.getCode());
        return Response.success(userMailResponse);
    }
}
