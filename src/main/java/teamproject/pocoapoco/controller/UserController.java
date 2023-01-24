package teamproject.pocoapoco.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.dto.user.*;
import teamproject.pocoapoco.security.provider.JwtProvider;
import teamproject.pocoapoco.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Api(tags = {"Join&Login Controller"})
public class UserController {

    private final UserService userService;

//    private final UserPhotoService userPhotoService;


    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "")
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
    public Response userInfoModify(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody UserProfileRequest userProfileRequest){

        UserProfileResponse userProfileResponse = userService.modifyMyUserInfo(token, userProfileRequest);
        return Response.success(userProfileResponse);

    }


    // 내 프로필 조회
    @GetMapping("/profile/my")
    public Response userMyInfoList(@RequestHeader("Authorization") String token){

        JwtProvider jwtProvider =new JwtProvider();
        Long myId = jwtProvider.getId(token);

        UserProfileResponse userProfileResponse = userService.selectUserInfo(myId);

        return Response.success(userProfileResponse);

    }

    // 상대방의 프로필 조회
    @GetMapping("/profile/{id}")
    public Response userInfoList(@PathVariable Long id){

        UserProfileResponse userProfileResponse = userService.selectUserInfo(id);
        return Response.success(userProfileResponse);

    }


}
