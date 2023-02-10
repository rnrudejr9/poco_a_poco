package teamproject.pocoapoco.controller.main.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.controller.main.api.UserController;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.dto.user.*;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;
import teamproject.pocoapoco.service.UserPhotoService;
import teamproject.pocoapoco.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Controller
@RequestMapping("/view/v1")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserPhotoService userPhotoService;
    private final EncrypterConfig encrypterConfig;


    @PostMapping("/users/profile/edit")
    public String editUser(@ModelAttribute UserProfileRequest userProfileRequest, Model model, Authentication authentication, HttpServletResponse response){

        log.info("닉네임: {}", userProfileRequest.getNickName());
        log.info("주소: {}", userProfileRequest.getAddress());
        log.info("기존 이름: {}", authentication.getName());

        User user = userService.updateUserInfoByUserName(authentication.getName(), userProfileRequest);


        UserProfileResponse userProfileResponse = UserProfileResponse.fromEntity(user);

        model.addAttribute("userProfileResponse", userProfileResponse);

        
//        UserLoginResponse tokens = userService.loginAgain(UserLoginRequest.builder()
//                .userId(user.getUserId())
//                .password(user.getPassword())
//                .build());
//
//        Cookie cookie = new Cookie("jwt", "Bearer+" + tokens.getAccessToken());
//
//        cookie.setPath("/");
//
//        cookie.setHttpOnly(true);
//        cookie.setMaxAge(60 * 25); //초단위 25분설정
//        response.addCookie(cookie);
//
//
//        // 레디스 키 값 삭제하는 로직 넣어주기


        return "redirect:/view/v1/users/profile/my";
    }



    @GetMapping("/users/profile/edit")
    public String editUserPage(Model model, Authentication authentication){

        String userName = authentication.getName();

        UserProfileResponse userProfileResponse = userService.getUserInfoByUserName(userName);

        String userProfileImagePath = userService.getProfilePathByUserName(userName);

        model.addAttribute("userProfileResponse", userProfileResponse);
        model.addAttribute("userProfileRequest", new UserProfileRequest());
        model.addAttribute("userProfileImagePath", userProfileImagePath);


        return "profile/edit";
    }

    @GetMapping("/users/profile/my")
    public String getMyProfile(Model model, Authentication authentication){

        String userName = authentication.getName();

        String userProfileImagePath = userService.getProfilePathByUserName(userName);

        UserProfileResponse userProfileResponse = userService.getUserInfoByUserName(userName);

        model.addAttribute("userProfileResponse", userProfileResponse);

        model.addAttribute("userProfileImagePath", userProfileImagePath);


        return "profile/get-my-profile";
    }



    @GetMapping("/users/profile/{userName}")
    public String getUserProfile(@PathVariable String userName, Model model, HttpServletResponse response) throws IOException {

        try{
            UserProfileResponse userProfileResponse = userService.getUserInfoByUserName(userName);
            model.addAttribute("userProfileResponse", userProfileResponse);
            return "profile/get";
        } catch (AppException e){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<script>alert('프로필 조회를 실패했습니다.'); history.go(-1); </script>");

            out.flush();
            return null;
        }


    }


    @ResponseBody
    @GetMapping("/users/check_duplicate/{userName}")
    public Boolean checkUserNameDuplicated(@PathVariable String userName, Model model, HttpServletResponse response) throws IOException {

        log.info(userName);

        try{
            Boolean userNameNotDuplicate = userService.checkNickNameDuplicated(userName);
//            model.addAttribute("userNameNotDupliacte", userNameNotDuplicate);

            log.info(String.valueOf(userNameNotDuplicate));

            return userNameNotDuplicate ; // 어떤 페이지로 연결할 지 생각해보기

        } catch (AppException e){

            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<script>alert('중복된 아이디가 존재합니다.');</script>");

            out.flush();
            return false;
        }

    }




    @GetMapping("/users/profile/check_duplicate_window")
    public String openDuplicateWindow(){

        return "profile/check-duplicated-username";
    }




    @PostMapping("/users/profile/image/edit")
    public String uploadImage(String imagePath, Authentication authentication, Model model){

        log.info(imagePath);

        String userName = authentication.getName();


        UserProfileResponse userProfileResponse = userPhotoService.editUserImage(userName, imagePath);

        model.addAttribute("userProfileImagePath", imagePath);

        model.addAttribute("userProfileResponse", userProfileResponse);


        return "profile/get-my-profile";

    }


    @GetMapping("/users/profile/image/edit")
    public String uploadImagePage(){


        return "/profile/upload-form";
    }








}
