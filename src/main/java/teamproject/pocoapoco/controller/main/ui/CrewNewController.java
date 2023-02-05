package teamproject.pocoapoco.controller.main.ui;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewSportRequest;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.CommentService;
import teamproject.pocoapoco.service.CrewService;
import teamproject.pocoapoco.service.LikeService;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CrewNewController {
    private final CrewService crewService;
    private final CommentService commentService;
    private final LikeService likeService;


    // 크루 게시글 등록
    @PostMapping("/view/v1/crews/")
    @ApiOperation(value = "크루 게시글 등록", notes = "")
    public String addCrew(@RequestBody CrewRequest crewRequest, Authentication authentication) {
        crewService.addCrew(crewRequest, authentication.getName());
        return "crew/write";
    }
    // 크루 게시물 상세 조회
    @GetMapping("/view/v1/crews/{crewId}")
    public String detailCrew(@PathVariable Long crewId, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest, Authentication authentication, Model model) {

        log.info("Strict : {}", crewSportRequest.getStrict());

        List<String> sportsList = crewSportRequest.getSportsList();
        if (CollectionUtils.isEmpty(sportsList))
            log.info("상세조회 list empty");
        else{
            log.info("상세조회 Sports list");
            for( String s : sportsList){
                log.info(s);
            }
        }
        Page<CrewDetailResponse> list;

        if(crewSportRequest.getStrict() == null && CollectionUtils.isEmpty(sportsList)){
            log.info("Detail if : All");
            list = crewService.findAllCrews(null);
        }
        else if(crewSportRequest.getStrict() != null){
            log.info("Detail if : Strict");
            list = crewService.findAllCrewsWithStrict(crewSportRequest, null);
        }
        else{
            log.info("Detail if : Sports list");
            list = crewService.findAllCrewsBySport(crewSportRequest, null);
        }




        try {
            CrewDetailResponse details = crewService.detailCrew(crewId);
            model.addAttribute("details", details);

/*
            likeService.updateLike(crewId,authentication.getName());
            int likeCount = likeService.selectLike(crewId);
            model.addAttribute("likeCount", likeCount);
*/

        } catch (EntityNotFoundException e) {
            return "redirect:/index";
        }
        return "crew/crewDetail2";
    }

}
