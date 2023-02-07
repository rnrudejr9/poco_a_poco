package teamproject.pocoapoco.controller.main.api.part;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
        return participationService.participate(partDto.getCrewId(), authentication.getName());
    }

    @GetMapping("/{crewId}")
    public Response findParticipate(@PathVariable Long crewId){
        log.info("#1 participateController crewId: "+ crewId);

        return Response.success(participationService.findParticipate(crewId));
    }
}
