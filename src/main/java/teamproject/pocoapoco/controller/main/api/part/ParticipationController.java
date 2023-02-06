package teamproject.pocoapoco.controller.main.api.part;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamproject.pocoapoco.domain.dto.part.PartDto;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.part.ParticipationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/part")
@Slf4j
public class ParticipationController {
    private final ParticipationService participationService;
    @PostMapping()
    public Response participate(@RequestBody PartDto partDto, Authentication authentication){
        log.info("#1 participateController crewId: "+ partDto.getCrewId());
        participationService.participate(partDto.getCrewId(), authentication.getName());
        return Response.success("참여하기 기능 동작");
    }
}
