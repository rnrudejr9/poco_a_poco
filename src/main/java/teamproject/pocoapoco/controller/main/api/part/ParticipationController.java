package teamproject.pocoapoco.controller.main.api.part;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.part.PartDto;
import teamproject.pocoapoco.domain.dto.part.PartJoinDto;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.part.ParticipationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/part")
@Slf4j
public class ParticipationController {
    private final ParticipationService participationService;

    @PostMapping("/gen")
    public Response generatePart(@RequestBody PartDto partDto, Authentication authentication){
        return participationService.generatePart(partDto, authentication.getName());
    }

    @PostMapping()
    public Response participate(@RequestBody PartJoinDto partDto){
        log.info("#1 participateController crewId: "+ partDto.getCrewId());
        return participationService.participate(partDto);
    }
    @PostMapping("/reject")
    public Response reject(@RequestBody PartJoinDto partDto){
        log.info("#1 participateController crewId: "+ partDto.getCrewId());
        return participationService.reject(partDto);
    }

    @GetMapping("/sign")
    public Response notAllowedPart(Authentication authentication){
        return Response.success(participationService.notAllowedMember(authentication.getName()));
    }

    @GetMapping("/{crewId}")
    public Response findParticipate(@PathVariable Long crewId, Authentication authentication){
        log.info("#1 participateController crewId: "+ crewId);
        return Response.success(participationService.findParticipate(crewId, authentication.getName()));
    }

    @GetMapping("/find/{crewId}")
    public Response findCrewInfo(@PathVariable Long crewId){
        return Response.success(participationService.findCrewInfo(crewId));
    }

    @GetMapping("/members/{crewId}")
    public Response findJoinMember(@PathVariable long crewId){
        log.info("#1 participateController crewId: "+ crewId);
        return Response.success(participationService.AllowedMember(crewId));
    }
}
