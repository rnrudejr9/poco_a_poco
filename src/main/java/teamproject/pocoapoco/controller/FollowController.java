package teamproject.pocoapoco.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.user.UserLoginRequest;
import teamproject.pocoapoco.domain.user.UserLoginResponse;
import teamproject.pocoapoco.service.FollowService;
import teamproject.pocoapoco.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/social")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    public Response<?> follow(@PathVariable String userId, Authentication authentication){

     return Response.success(followService.follow(authentication.getName(),userId));
    }

    @PostMapping("/{userId}/unfollow")
    public Response<?> unFollow(@PathVariable String userId, Authentication authentication){

        return Response.success(followService.unFollow(authentication.getName(),userId));
    }
}
