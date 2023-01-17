package teamproject.pocoapoco.enums.login.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.enums.login.logindto.UserLoginRequest;
import teamproject.pocoapoco.enums.login.logindto.Response;
import teamproject.pocoapoco.enums.login.service.LoginService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class LoginController {

    private final LoginService loginService;

    // 로그인
    @PostMapping("/login")
    public Response login(@RequestBody UserLoginRequest userLoginRequest) throws Exception {
        return loginService.login(userLoginRequest);
    }
}
