package teamproject.pocoapoco.controller.main.ui;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewStrictRequest;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.entity.Sport;
import teamproject.pocoapoco.service.CrewService;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/v1/crews")
@Slf4j
@Api(tags = {"Crew Controller"})
public class CrewViewController {

    private final CrewService crewService;

    // 크루 게시물 전체 조회
    @GetMapping()
    @ApiOperation(value = "크루 게시글 전체조회", notes = "")
    public String findAllCrew(Model model,
                                @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CrewDetailResponse> list = crewService.findAllCrews(pageable);

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());
        int lastPage = list.getTotalPages();

        model.addAttribute("crewList", list);

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("lastPage", lastPage);

        return "main/main";
    }


    // 크루 게시물 지역 검색 조회
    @PostMapping("/strict")
    @ApiOperation(value = "크루 게시글 지역 검색조회", notes = "")
    public String findAllCrewWithStrict(String strict, Model model,
                                          @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        CrewStrictRequest crewStrictRequest = new CrewStrictRequest(strict);
        System.out.println("test\n" + crewStrictRequest.getStrict());
        Page<CrewDetailResponse> list = crewService.findAllCrewsWithStrict(crewStrictRequest, pageable);

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());
        int lastPage = list.getTotalPages();

        model.addAttribute("crewList", list);

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("lastPage", lastPage);

        return "main/main";
    }

    @PostMapping("/test")
    @ApiOperation(value = "크루 게시글 지역 검색조회", notes = "")
    public String test(@ModelAttribute boolean open) {

        log.info("testing");
        return "main/main";
    }

}
