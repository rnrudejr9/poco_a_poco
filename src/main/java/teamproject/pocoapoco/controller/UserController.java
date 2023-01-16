package teamproject.pocoapoco.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
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

    @PostMapping("/join")
    public ResponseEntity<UserJoinResponse> userAdd(@RequestBody UserJoinRequest request){

        UserJoinResponse response = userService.addUser(request);

        return ResponseEntity.ok().body(response);

    }

}
