package teamproject.pocoapoco.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.CommentService;
import teamproject.pocoapoco.service.CrewService;
import teamproject.pocoapoco.service.LikeService;

import javax.persistence.EntityNotFoundException;

@Controller
@RequiredArgsConstructor
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
    public String detailCrew(@PathVariable Long crewId,Authentication authentication, Model model) {
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
