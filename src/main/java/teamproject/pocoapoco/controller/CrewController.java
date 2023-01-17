package teamproject.pocoapoco.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.crew.CrewAddRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewAddResponse;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.service.CrewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews")
@Slf4j
public class CrewController {

    private final CrewService crewService;


    // Crew 게시글 등록
    @PostMapping()
    public Response addCrew(@RequestBody CrewAddRequest request, Authentication authentication) {
        log.info(request.getTitle());
        CrewAddResponse response = crewService.addCrew(request, authentication.getName());
        return Response.success(response);
    }

}
