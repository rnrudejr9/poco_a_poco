package teamproject.pocoapoco.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
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

import java.util.List;

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
    public Response updateCrew(@PathVariable Long crewId, @RequestBody CrewRequest crewRequest, Authentication authentication) {

        CrewResponse response = crewService.updateCrew(crewId, crewRequest, authentication.getName());

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
    public Response getAllCrews(@PageableDefault(page = 0,
            size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        List<CrewDetailResponse> crews = crewService.allCrew(pageable);

        return Response.success(new PageImpl<>(crews));
    }

    // 크루 게시물 지역 검색 조회
    @PostMapping("/strict")
    public Response getAllCrewWithStrict(@RequestBody CrewStrictRequest crewStrictRequest,
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        List<CrewDetailResponse> crews = crewService.allCrewWithSport(crewStrictRequest, pageable);

        return Response.success(new PageImpl<>(crews));
    }
}
