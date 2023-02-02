package teamproject.pocoapoco.controller.main.ui;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.crew.*;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.service.CrewService;

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
    public String findAllCrew(Model model, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
                              @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("GetMapping findAllCrew");
        log.info("crewSportRequest : {}", crewSportRequest.getStrict());

        List<String> sportsList = crewSportRequest.getSportsList();

        if (CollectionUtils.isEmpty(sportsList))
            log.info("list empty");

        Page<CrewDetailResponse> list;
        if(crewSportRequest.getStrict() == null)
            list = crewService.findAllCrews(pageable);
        else if(crewSportRequest.getStrict() != null)
            list = crewService.findAllCrewsWithStrict(crewSportRequest, pageable);
        else
            list = crewService.findAllCrewsBySport(crewSportRequest, pageable);


        // 페이징 처리 변수
        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());
        int lastPage = list.getTotalPages();

        // 게시글 리스트
        model.addAttribute("crewList", list);

        // 페이징 처리 모델
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("lastPage", lastPage);


        //test : 종목 검색
        model.addAttribute("sportRequest",  crewSportRequest);

        return "main/main";
    }


    //test : 종목 검색
    @PostMapping()
    public String findAllCrewAndSport(Model model, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
    @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("PostMapping findAllCrewAndSport");
        log.info(String.valueOf(crewSportRequest.getSportsList().isEmpty()));

        Page<CrewDetailResponse> list = crewService.findAllCrewsBySport(crewSportRequest, pageable);

        // 페이징 처리 변수
        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());
        int lastPage = list.getTotalPages();

        // 게시글 리스트
        model.addAttribute("crewList", list);

        // 페이징 처리 모델
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("sportRequest", crewSportRequest);


        return "main/main";
    }


    @ModelAttribute("sportEnums")
    private SportEnum[] sportEnums() {

        SportEnum[] sportEnum = SportEnum.values();
        System.out.println(sportEnum);

        return sportEnum;
    }

}
