package teamproject.pocoapoco.controller.main.ui;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.service.CrewService;
import teamproject.pocoapoco.service.manage.DashboardService;
import teamproject.pocoapoco.service.manage.ManagerService;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@Slf4j
@RequestMapping("/view/v1")
@RequiredArgsConstructor
@Transactional
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
    public String manageCrews(Model model, @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Long crewCountByStrict = dashboardService.getCrewCount();
        model.addAttribute("crewCountByStrict", crewCountByStrict);

        Page<CrewManageResponse> crewManageResponsePage = managerService.getCrewInfo(pageable);
        model.addAttribute("crewManageResponsePage", crewManageResponsePage);

        log.info("pageable:{}", crewManageResponsePage.getPageable());
        log.info("pagesize: {}", crewManageResponsePage.getPageable().getPageSize());
        log.info("nowpage: {}", crewManageResponsePage.getPageable().getPageNumber());


        int nowPage = crewManageResponsePage.getPageable().getPageNumber() + 1;
        int startNumPage = Math.max(nowPage - 4, 1);
        int endNumPage = Math.min(nowPage + 5, crewManageResponsePage.getTotalPages());
        int lastPage = crewManageResponsePage.getTotalPages();

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startNumPage", startNumPage);
        model.addAttribute("endNumPage", endNumPage);
        model.addAttribute("lastPage", lastPage);


        return "dashboard/crew-manage";
    }


    @DeleteMapping("/manage/crews/{crewId}")
    @ApiOperation(value = "크루 게시글 삭제", notes = "")
    public String deleteCrew(@PathVariable Long crewId, Authentication authentication, Model model, HttpServletResponse response) throws IOException {


        try{
            CrewResponse crewResponse = null;
            try {
                crewResponse = crewService.deleteCrew(crewId, authentication.getName());
            }catch (NullPointerException e){
                log.error("null Error");
                return "redirect:/";
            }
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


    @GetMapping("/manage/crews/{crewId}/{userId}/delete")
    @ApiOperation(value = "모임에서 회원 강제 퇴장", notes = "")
    public String deleteUserFromCrew(@PathVariable Long crewId, @PathVariable Long userId, Authentication authentication, Model model, HttpServletResponse response) throws IOException {

        try{
            crewService.deleteUserAtCrew(userId, crewId, authentication.getName());


            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('참여자가 강퇴 되었습니다.'); </script>");

            return "redirect:/view/v1/crews";

        } catch (AppException e){

            log.info("오류 코드: {}", e.getErrorCode());
            log.info("오류 메시지: {}", e.getMessage());

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
