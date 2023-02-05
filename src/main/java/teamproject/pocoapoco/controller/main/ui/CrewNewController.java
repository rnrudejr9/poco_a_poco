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
import java.util.Optional;
import java.util.stream.Collectors;

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
    public String detailCrew(@PathVariable Long crewId, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest, Authentication authentication, Model model,
                             @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

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

        if(crewSportRequest.getStrict() == "" && CollectionUtils.isEmpty(sportsList)){
            log.info("Detail if : All");
            list = crewService.findAllCrews(pageable);
        }
        else if(crewSportRequest.getStrict() != null && crewSportRequest.getStrict() != ""){
            log.info("Detail if : Strict");
            list = crewService.findAllCrewsWithStrict(crewSportRequest, pageable);
        }
        else{
            log.info("Detail if : Sports list");
            list = crewService.findAllCrewsBySport(crewSportRequest, pageable);
        }


        List<Long> crewIdList = list.getContent().
                stream().
                map(c -> c.getId())
                .collect(Collectors.toList());

        Optional<CrewDetailResponse> testList = list.getContent().stream()
                .filter(c -> c.getId() == crewId)
                .findFirst();

        if(testList.isPresent())
            log.info("testList : {}", testList.get());
        else
            log.info("testList : empty");










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
