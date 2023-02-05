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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.crew.*;
import teamproject.pocoapoco.domain.dto.user.UserProfileResponse;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.service.CrewService;
import teamproject.pocoapoco.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/v1/crews")
@Slf4j
@Api(tags = {"Crew Controller"})
public class CrewViewController {

    private final CrewService crewService;

    private final UserService userService;

    // 크루 게시물 전체 조회, 검색 조회, 운동 종목 조회
    @GetMapping()
    @ApiOperation(value = "크루 게시글 전체조회", notes = "")
    public String findAllCrew(Model model, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest, Authentication authentication,
                              @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        // 로그 확인
        log.info("GetMapping findAllCrew");

        if(authentication != null)
            log.info("authentication.getName() :" + authentication.getName());
        else
            log.info("authentication : null");
        
        log.info("Strict : {}", crewSportRequest.getStrict());

        List<String> sportsList = crewSportRequest.getSportsList();
        if (CollectionUtils.isEmpty(sportsList))
            log.info("list empty");
        else{
            log.info("Sports list");
            for( String s : sportsList){
                log.info(s);
            }
        }
        log.info("\n");


        // 유저 로그인 확인
        if(authentication != null  && CollectionUtils.isEmpty(sportsList)) {

            log.info("authentication.getName() :" + authentication.getName());

            UserProfileResponse userProfileResponse = userService.getUserInfoByUserName(authentication.getName());
            List<String> userSportsList = new ArrayList<>();

            if(userProfileResponse.getLikeSoccer()){
                userSportsList.add("SOCCER");
            }
            if(userProfileResponse.getLikeJogging()){
                userSportsList.add("JOGGING");
            }
            if(userProfileResponse.getLikeTennis()){
                userSportsList.add("TENNIS");
            }

            crewSportRequest.setSportsList(userSportsList);

            //확인용
            model.addAttribute("userSportsList", userSportsList);
        }
        else {
            log.info("authentication : null");
        }




        
        // 페이징 검색 필터
        Page<CrewDetailResponse> list;

        if(crewSportRequest.getStrict() == null && CollectionUtils.isEmpty(sportsList)){
            log.info("if : All");
            list = crewService.findAllCrews(pageable);
        }
        else if(crewSportRequest.getStrict() != null && crewSportRequest.getStrict() != ""){
            log.info("if : Strict");
            list = crewService.findAllCrewsWithStrict(crewSportRequest, pageable);
        }
        else{
            log.info("if : Sports list");
            list = crewService.findAllCrewsBySport(crewSportRequest, pageable);
        }


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

        // [임시 유저 데이터] 유저 데이터를 가지고 오면 미리 체크를 한다.
//        List<String> userSportsList = new ArrayList<>();
//        userSportsList.add(String.valueOf(SportEnum.JOGGING));
//        userSportsList.add(String.valueOf(SportEnum.SOCCER));
//        crewSportRequest.setSportsList(userSportsList);


        List<Long> crewIdList = list.getContent().
                stream().
                map(c -> c.getId())
                .collect(Collectors.toList());
        for(Long s : crewIdList)
            log.info("{}", s);
        model.addAttribute("crewIdList", crewIdList);






        return "main/main";
    }

    @ModelAttribute("sportEnums")
    private SportEnum[] sportEnums() {

        SportEnum[] sportEnum = SportEnum.values();
        System.out.println(sportEnum);

        return sportEnum;
    }



}
