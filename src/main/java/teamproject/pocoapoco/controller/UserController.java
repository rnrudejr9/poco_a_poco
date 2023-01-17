package teamproject.pocoapoco.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
import teamproject.pocoapoco.domain.user.UserLoginRequest;
import teamproject.pocoapoco.domain.user.UserLoginResponse;
import teamproject.pocoapoco.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


    @PostMapping("/login")
    public Response userLogin(@RequestBody UserLoginRequest userLoginRequest){

        UserLoginResponse userLoginResponse = userService.login(userLoginRequest);

        return Response.success(userLoginResponse);

    }
    @PostMapping("/join")
    public ResponseEntity<UserJoinResponse> userAdd(@RequestBody UserJoinRequest request){

        UserJoinResponse response = userService.addUser(request);

        return ResponseEntity.ok().body(response);

    }

}
