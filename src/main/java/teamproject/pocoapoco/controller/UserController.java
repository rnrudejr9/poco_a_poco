package teamproject.pocoapoco.controller;

import io.swagger.models.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.*;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.provider.JwtProvider;
import teamproject.pocoapoco.service.UserPhotoService;
import teamproject.pocoapoco.service.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

//    private final UserPhotoService userPhotoService;


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
    public Response userAdd(@RequestBody UserJoinRequest request){

        UserJoinResponse userJoinResponse = userService.addUser(request);

        return Response.success(userJoinResponse);

    }


    // 내 프로필 수정
    @PutMapping("/revise")
    public Response userInfoModify(Authentication authentication, @RequestBody UserProfileRequest userProfileRequest){

        UserProfileResponse userProfileResponse = userService.modifyMyUserInfo(authentication.getName(), userProfileRequest);
        return Response.success(userProfileResponse);

    }


    // 내 프로필 조회
    @GetMapping("/profile/my")
    public Response userMyInfoList(Authentication authentication){

        UserProfileResponse userProfileResponse = userService.selectUserInfo(authentication.getName());

        return Response.success(userProfileResponse);

    }

    // 상대방의 프로필 조회
    @GetMapping("/profile/{userName}")
    public Response userInfoList(@PathVariable String userName){


        UserProfileResponse userProfileResponse = userService.selectUserInfo(userName);
        return Response.success(userProfileResponse);

    }


}
