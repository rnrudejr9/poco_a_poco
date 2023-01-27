package teamproject.pocoapoco.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.dto.user.*;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Api(value = "회원가입, 로그인, 프로필 조회 등 사용자와 관련된 기능이 있는 controller 입니다.")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

//    private final UserPhotoService userPhotoService;

    @ApiOperation(value = "hello controller", notes = "시험용 컨트롤러")
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @ApiOperation(value = "로그인", notes = "로그인 시 Access Token, Refresh Token을 발행합니다.")
    @PostMapping("/login")
    public Response userLogin(@RequestBody UserLoginRequest userLoginRequest){

        UserLoginResponse userLoginResponse = userService.login(userLoginRequest);

        return Response.success(userLoginResponse);

    }

    @ApiOperation(value = "회원가입", notes = "회원가입하는 메소드입니다.")
    @PostMapping("/join")
    public Response userAdd(@RequestBody UserJoinRequest request){

        UserJoinResponse userJoinResponse = userService.saveUser(request);

        return Response.success(userJoinResponse);

    }


    // 내 프로필 수정
    @ApiOperation(value = "프로필 수정", notes = "프로필 수정하는 메소드입니다.")
    @PutMapping("/profile/revise")
    public Response userInfoModify(Authentication authentication, @RequestBody UserProfileRequest userProfileRequest){


        UserProfileResponse userProfileResponse = userService.updateUserInfoByUserName(authentication.getName(), userProfileRequest);

        return Response.success(userProfileResponse);

    }


    // 내 프로필 조회
    @ApiOperation(value = "내 프로필 조회", notes = "내 프로필을 조회하는 메소드입니다.")
    @GetMapping("/profile/my")
    public Response userMyInfoList(Authentication authentication){


        UserProfileResponse userProfileResponse = userService.getUserInfoByUserName(authentication.getName());


        return Response.success(userProfileResponse);

    }

    // 상대방의 프로필 조회
    @ApiOperation(value = "프로필 조회", notes = "상대방의 프로필을 조회하는 메소드입니다.")
    @GetMapping("/profile/{userName}")
    public Response userInfoList(@PathVariable String userName){


        UserProfileResponse userProfileResponse = userService.getUserInfoByUserName(userName);



        return Response.success(userProfileResponse);

    }


}
