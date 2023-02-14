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

    //참여 신청
    @PostMapping("/gen")
    public Response generatePart(@RequestBody PartDto partDto, Authentication authentication){
        return participationService.generatePart(partDto, authentication.getName());
    }

    //참여 승인
    @PostMapping()
    public Response participate(@RequestBody PartJoinDto partDto){
        log.info("#1 participateController crewId: "+ partDto.getCrewId());
        return participationService.participate(partDto);
    }

    //거절하기
    @PostMapping("/reject")
    public Response reject(@RequestBody PartJoinDto partDto){
        log.info("#1 participateController crewId: "+ partDto.getCrewId());
        return participationService.reject(partDto);
    }

    //승인되지 않은 멤버 출력
    @GetMapping("/sign")
    public Response notAllowedPart(Authentication authentication){
        return Response.success(participationService.notAllowedMember(authentication.getName()));
    }

    //참여유무 확인
    @GetMapping("/{crewId}")
    public Response findParticipate(@PathVariable Long crewId, Authentication authentication){
        log.info("#1 participateController crewId: "+ crewId);
        return Response.success(participationService.findParticipate(crewId, authentication.getName()));
    }


    //현재 크루 참여자수 확인
    @GetMapping("/find/{crewId}")
    public Response findCrewInfo(@PathVariable Long crewId){
        return Response.success(participationService.findCrewInfo(crewId));
    }

    //승인된 멤버조회
    @GetMapping("/members/{crewId}")
    public Response findJoinMember(@PathVariable long crewId){
        log.info("#1 participateController crewId: "+ crewId);
        return Response.success(participationService.AllowedMember(crewId));
    }

    @PostMapping("/finish")
    public Response finishPart(@RequestBody PartDto partDto){
        return Response.success(participationService.finishPart(partDto.getCrewId()));
    }
}
