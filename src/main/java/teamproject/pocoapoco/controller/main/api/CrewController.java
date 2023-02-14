package teamproject.pocoapoco.controller.main.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewSportRequest;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.CrewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews")
@Slf4j
@Api(tags = {"Crew Controller"})
public class CrewController {

    private final CrewService crewService;


    // 크루 게시글 등록
    @PostMapping()
    @ApiOperation(value = "크루 게시글 등록", notes = "")
    public Response addCrew(@RequestBody CrewRequest crewRequest, Authentication authentication) {
        return Response.success(crewService.addCrew(crewRequest, authentication.getName()));
    }

    // 크루 게시글 수정
    @PostMapping("/{crewId}")
    @ApiOperation(value = "크루 게시글 수정", notes = "")
    public Response modifyCrew(@PathVariable Long crewId, @RequestBody CrewRequest crewRequest, Authentication authentication) {
        return Response.success(crewService.modifyCrew(crewId, crewRequest, authentication.getName()));
    }

    // 크루 게시글 삭제
    @DeleteMapping("/{crewId}")
    @ApiOperation(value = "크루 게시글 삭제", notes = "")
    public Response deleteCrew(@PathVariable Long crewId, Authentication authentication) {
        return Response.success(crewService.deleteCrew(crewId, authentication.getName()));
    }

    @PostMapping("/finish")
    @ApiOperation(value = "크루 게시글 모임종료", notes = "")
    public Response finishCrew(@RequestBody CrewRequest crewRequest, Authentication authentication) {
        return Response.success(crewService.finishCrew(crewRequest.getId(), authentication.getName()));
    }

    // 크루 게시물 상세 조회
    @GetMapping("/{crewId}")
    @ApiOperation(value = "크루 게시글 상세조회", notes = "")
    public Response detailCrew(@PathVariable Long crewId, Authentication authentication) {
        return Response.success(crewService.detailCrew(crewId));
    }

    // 크루 게시물 전체 조회
    @GetMapping()
    @ApiOperation(value = "크루 게시글 전체조회", notes = "")
    public Response findAllCrew(@PageableDefault(page = 0,
            size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return Response.success(crewService.findAllCrews(pageable));
    }

    // 크루 게시물 지역 검색 조회
    @PostMapping("/strict")
    @ApiOperation(value = "크루 게시글 지역 검색조회", notes = "")
    public Response findAllCrewWithStrict(@RequestBody CrewSportRequest crewSportRequest,
                                          @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return Response.success(crewService.findAllCrewsByStrict(crewSportRequest, pageable));
    }
}
