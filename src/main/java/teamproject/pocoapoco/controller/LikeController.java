package teamproject.pocoapoco.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamproject.pocoapoco.domain.dto.like.LikeResponse;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.LikeService;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    @PostMapping("/{crewId}/like")
    public Response<LikeResponse> likeCrew(@PathVariable Long crewId, Authentication authentication){
        LikeResponse goodResponse = likeService.goodCrew(crewId,authentication.getName());
        return Response.success(goodResponse);
    }
}
