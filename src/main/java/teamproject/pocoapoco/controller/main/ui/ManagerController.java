package teamproject.pocoapoco.controller.main.ui;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import teamproject.pocoapoco.domain.dto.crew.CrewResponse;
import teamproject.pocoapoco.domain.dto.manage.CrewManageResponse;
import teamproject.pocoapoco.domain.dto.manage.UserManageResponse;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.dto.user.UserProfileResponse;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.service.CrewService;
import teamproject.pocoapoco.service.UserService;
import teamproject.pocoapoco.service.manage.DashboardService;
import teamproject.pocoapoco.service.manage.ManagerService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/view/v1")
@RequiredArgsConstructor
public class ManagerController {


    private final ManagerService managerService;
    private final DashboardService dashboardService;

    private final CrewService crewService;

    @GetMapping("/manage/users")
    public String manageUsers(Model model){
        Long currentUser = dashboardService.getUserCount();
        model.addAttribute("currentUser", currentUser);


        Page<UserManageResponse> userManageResponsePage = managerService.getUsersInfo();
        model.addAttribute("userManageResponsePage", userManageResponsePage);

        return "dashboard/user-manage";
    }


    @GetMapping("/manage/crews")
    public String manageCrews(Model model,String strict, @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<CrewManageResponse> crewManageResponsePage = managerService.getCrewInfo(strict, pageable);
        model.addAttribute("crewManageResponsePage", crewManageResponsePage);


        return "dashboard/crew-manage";
    }


    @DeleteMapping("/manage/crews/{crewId}")
    @ApiOperation(value = "크루 게시글 삭제", notes = "")
    public String deleteCrew(@PathVariable Long crewId, Authentication authentication, Model model, HttpServletResponse response) throws IOException {


        try{
            CrewResponse crewResponse = crewService.deleteCrew(crewId, authentication.getName());
            model.addAttribute("message", crewResponse.getCrewId() + "번 모임을 삭제했습니다.");
            return "redirect:/view/v1/manage/crews";

        } catch (AppException e){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<script>alert('모임 삭제 권한이 없습니다.'); history.go(-1); </script>");

            out.flush();
            return null;
        } catch (Exception e){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<script>alert('모임 삭제를 실패했습니다.'); history.go(-1); </script>");

            out.flush();
            return null;

        }


    }

    @DeleteMapping("/manage/crews/{crewId}/{userName}")
    @ApiOperation(value = "모임에서 회원 강제 퇴장", notes = "")
    public String deleteUserFromCrew(@PathVariable Long crewId, @PathVariable String userName,Authentication authentication, Model model, HttpServletResponse response) throws IOException {

        try{
            model.addAttribute("message", userName+"님을 " + crewId + "번 모임에서 강제 퇴장했습니다.");
            return "redirect:/view/v1/manage/crews";

        } catch (AppException e){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<script>alert('참여자 강제 퇴장 권한이 없습니다.'); history.go(-1); </script>");

            out.flush();
            return null;
        } catch (Exception e){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<script>alert('사용자 강제 퇴장을 실패했습니다.'); history.go(-1); </script>");

            out.flush();
            return null;

        }


    }




}
