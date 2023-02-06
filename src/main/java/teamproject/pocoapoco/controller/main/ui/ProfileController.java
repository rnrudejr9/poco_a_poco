package teamproject.pocoapoco.controller.main.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.controller.main.api.UserController;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.dto.user.UserProfileImageRequest;
import teamproject.pocoapoco.domain.dto.user.UserProfileRequest;
import teamproject.pocoapoco.domain.dto.user.UserProfileResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.UserPhotoService;
import teamproject.pocoapoco.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/view/v1")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserPhotoService userPhotoService;


    @PostMapping("/users/profile/edit")
    public String editUser(@ModelAttribute UserProfileRequest userProfileRequest, Model model, Authentication authentication){

        UserProfileResponse userProfileResponse = userService.updateUserInfoByUserName(authentication.getName(), userProfileRequest);

        model.addAttribute("userProfileResponse", userProfileResponse);


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
            Boolean userNameNotDuplicate = userService.checkUserNameDuplicate(userName);
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
