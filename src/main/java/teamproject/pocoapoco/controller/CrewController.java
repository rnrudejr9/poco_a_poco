package teamproject.pocoapoco.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewStrictRequest;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.CrewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews")
@Slf4j
public class CrewController {

    private final CrewService crewService;


    // 크루 게시글 등록
    @PostMapping()
    public Response addCrew(@RequestBody CrewRequest crewRequest, Authentication authentication) {

        CrewResponse response = crewService.addCrew(crewRequest, authentication.getName());

        return Response.success(response);
    }

    // 크루 게시글 수정
    @PostMapping("/{crewId}")
    public Response modifyCrew(@PathVariable Long crewId, @RequestBody CrewRequest crewRequest, Authentication authentication) {

        CrewResponse response = crewService.modifyCrew(crewId, crewRequest, authentication.getName());

        return Response.success(response);
    }

    // 크루 게시글 삭제
    @DeleteMapping("/{crewId}")
    public Response deleteCrew(@PathVariable Long crewId, Authentication authentication) {

        CrewResponse response = crewService.deleteCrew(crewId, authentication.getName());

        return Response.success(response);
    }

    // 크루 게시물 상세 조회
    @GetMapping("/{crewId}")
    public Response detailCrew(@PathVariable Long crewId, Authentication authentication) {

        CrewDetailResponse response = crewService.detailCrew(crewId, authentication.getName());

        return Response.success(response);
    }

    // 크루 게시물 전체 조회
    @GetMapping()
    public Response findAllCrew(@PageableDefault(page = 0,
            size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CrewDetailResponse> crews = crewService.findAllCrews(pageable);

        return Response.success(crews);
    }

    // 크루 게시물 지역 검색 조회
    @PostMapping("/strict")
    public Response findAllCrewWithStrict(@RequestBody CrewStrictRequest crewStrictRequest,
                                          @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CrewDetailResponse> crews = crewService.findAllCrewsWithStrict(crewStrictRequest, pageable);

        return Response.success(crews);
    }
}
