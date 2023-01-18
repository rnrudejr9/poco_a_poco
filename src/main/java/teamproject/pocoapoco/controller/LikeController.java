package teamproject.pocoapoco.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamproject.pocoapoco.domain.dto.like.LikeResponse;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.LikeService;

@RestController
@RequestMapping("/api/v1/crew")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{crewId}/like")
    public Response<LikeResponse> likeCrew(@PathVariable Long crewId){
        Long loginId = 1L; //  임의로그인한 아이디
        LikeResponse goodResponse = likeService.goodCrew(crewId,loginId);
        return Response.success(goodResponse);
    }
}
