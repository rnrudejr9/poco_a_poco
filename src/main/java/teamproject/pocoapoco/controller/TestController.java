package teamproject.pocoapoco.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.dto.user.UserProfileRequest;
import teamproject.pocoapoco.domain.dto.user.UserProfileResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/view/v1")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/crews/write")
    public String hello(){
        return "crew/write";
    }


    @PostMapping("/users/profile/edit")
    public String editUser(@ModelAttribute UserProfileRequest userProfileRequest, Model model, Authentication authentication){

        Response response = new UserController(userService, userRepository).userInfoModify(authentication, userProfileRequest);

        if (response.getResultCode().equals("SUCCESS")){
            model.addAttribute("userProfileResponse", response.getResult());
        }


        return "profile/edit";
    }

    @GetMapping("/users/profile/my")
    public String getMyProfile(Model model, HttpServletRequest httpServletRequest){
//        String username = (String) httpServletRequest.getAttribute("username");

        UserProfileResponse userProfileResponse = userService.getUserInfoByUserName("string");

        model.addAttribute("userProfileResponse", userProfileResponse);


        return "profile/edit";
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

}
