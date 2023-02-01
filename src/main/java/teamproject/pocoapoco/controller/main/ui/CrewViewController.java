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
import teamproject.pocoapoco.domain.dto.crew.*;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.entity.Sport;
import teamproject.pocoapoco.service.CrewService;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        // 페이징 처리 변수
        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());
        int lastPage = list.getTotalPages();

        // 모집글 리스트
        model.addAttribute("crewList", list);

        // 페이징 처리 모델
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("lastPage", lastPage);

        //test : 종목 검색
        model.addAttribute("sport", new SportRequest());
        log.info("get sport");

        return "main/main";
    }


    //test : 종목 검색
    @PostMapping()
    public String findAllCrewAndSport(Model model, @ModelAttribute("sport") SportRequest sportRequest,
                                      @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        //test
        log.info("post sport");
        log.info("post sport soccer : {}", sportRequest.isSoccer());
        log.info("post sport tennis : {}", sportRequest.isTennis());
        log.info("post sport jogging : {}", sportRequest.isJogging());


        Page<CrewDetailResponse> list = crewService.findAllCrewsBySport(pageable);

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


    //testㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("form", new FormDto());
        log.info("get");
        return "main/test";
    }

//    @PostMapping("/form")
//    public String form(@ModelAttribute("form") FormDto formDto) {
//        log.info("post / {}", formDto.getName());
//        log.info("post / {}", formDto.isTnf());
//        return "main/test";
//    }

    @PostMapping("/form")
    public String form2(@ModelAttribute("form") FormDto formDto) {
        log.info("formDto.name = {}", formDto.getName());
        log.info("formDto.tnf = {}", formDto.isTnf());
        List<String> hobbies = formDto.getHobbies();
        for (String hobby : hobbies) {
            log.info("formDto.hobby = {}", hobby);
        }

        return "main/test";
    }


    @ModelAttribute("hobbies")
    private Map<String, String> favorite() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("movie", "영화보기");
        map.put("music", "음악듣기");
        map.put("running", "런닝하기");
        map.put("game", "게임하기");
        return map;
    }


}
