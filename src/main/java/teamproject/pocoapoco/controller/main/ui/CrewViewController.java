package teamproject.pocoapoco.controller.main.ui;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import teamproject.pocoapoco.domain.dto.crew.*;
import teamproject.pocoapoco.domain.dto.crew.members.CrewMemberDeleteResponse;
import teamproject.pocoapoco.domain.dto.crew.members.CrewMemberResponse;
import teamproject.pocoapoco.domain.dto.like.LikeViewResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.CrewMemberService;
import teamproject.pocoapoco.service.CrewService;
import teamproject.pocoapoco.service.LikeViewService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/v1/crews")
@Slf4j
@Api(tags = {"Crew Controller"})
public class CrewViewController {

    private final CrewService crewService;
    private final LikeViewService likeViewService;
    private final CrewMemberService crewMemberService;
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;

    // 크루 게시물 상세 페이지
    @GetMapping("/{crewId}")
    public String detailCrew(@PathVariable Long crewId, Model model) {
        try {
            CrewDetailResponse details = crewService.detailCrew(crewId);
            int count = likeViewService.getLikeCrew(crewId);

            model.addAttribute("details", details);
            // 좋아요 개수 출력
            model.addAttribute("likeCnt",count);
        } catch (EntityNotFoundException e) {
            return "redirect:/index";
        }
        return "crew/read-crew";
    }

    // 크루 게시글 수정
    @PutMapping("/{crewId}")
    public String modifyCrew(@PathVariable Long crewId, @ModelAttribute CrewRequest crewRequest, Authentication authentication, Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            return "crew/update-crew";
        }
        CrewResponse crewResponse = crewService.modifyCrew(crewId, crewRequest, authentication.getName());
        attributes.addFlashAttribute("message", "게시글을 수정했습니다.");

        return "redirect:" + "/view/v1/crews/"+ crewResponse.getCrewId();
    }

    // 크루 게시글 수정화면
    @GetMapping("/update/{crewId}")
    public String updateCrew (@PathVariable Long crewId, Model model, Authentication authentication) {
        Crew crew  = crewRepository.findById(crewId).orElse(null);
        if(crew == null || !crew.getUser().getUsername().equals(authentication.getName())) {
            return "error/404";
        }
        CrewRequest crewRequest = new CrewRequest();
        crewRequest.setTitle(crew.getTitle());
        crewRequest.setContent(crew.getContent());

        model.addAttribute(crewRequest);
        model.addAttribute("crewId", crew.getId());

        return "crew/update-crew";
    }

    // 크루 게시글 삭제
    @DeleteMapping("/{crewId}")
    public String deleteCrew(@PathVariable Long crewId,Model model ,Authentication authentication) {
        Crew crew = crewRepository.findById(crewId).orElse(null);
        User user = userRepository.findById(crew.getUser().getId()).orElse(null);
        log.info("삭제 조회 중");

        if(crew == null || user == null) {
            return "error/404";
        }
        CrewResponse crewResponse = crewService.deleteCrew(crewId, authentication.getName());
        model.addAttribute("response",crewResponse);
        return "redirect:/";
    }

    // 크루 좋아요 누르기
    @PostMapping("/{crewId}/like")
    public ResponseEntity likeCrew(@PathVariable Long crewId, Authentication authentication){
        LikeViewResponse likeViewResponse =  likeViewService.pressLike(crewId,authentication.getName());
        return new ResponseEntity<>(likeViewResponse, HttpStatus.OK);
    }

    // 크루 참여한 MemberList 출력
    @GetMapping("/{crewId}/joinCrew")
    public ResponseEntity getJoinList(@PathVariable Long crewId){
        List<CrewMemberResponse> list = crewMemberService.getJoinMemberList(crewId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    // 크루 참여하기
    @PostMapping("/{crewId}/joinCrew")
    public ResponseEntity joinCrew(@PathVariable Long crewId, Authentication authentication){
        CrewMemberResponse joinMemberResponse = crewMemberService.joinCrew(crewId,authentication.getName());
        return new ResponseEntity<>(joinMemberResponse, HttpStatus.OK);
    }
    // 크루 나가기
    @DeleteMapping("/{crewId}/leaveCrew")
    public ResponseEntity leaveCrew(@PathVariable Long crewId, Authentication authentication){
        CrewMemberDeleteResponse crewMemberDeleteResponse = crewMemberService.leaveCrew(crewId,authentication.getName());
        return new ResponseEntity<>(crewMemberDeleteResponse, HttpStatus.OK);
    }

    // 크루 게시물 전체 조회
    @GetMapping()
    @ApiOperation(value = "크루 게시글 전체조회", notes = "")
    public String findAllCrew(Model model, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
                              @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("GetMapping findAllCrew");

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

        Page<CrewDetailResponse> list;

        if(crewSportRequest.getStrict() == null && CollectionUtils.isEmpty(sportsList)){
            log.info("if : All");
            list = crewService.findAllCrews(pageable);
        }
        else if(crewSportRequest.getStrict() != "null" && CollectionUtils.isEmpty(sportsList)){
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



    @GetMapping("/test")
    public String findAllCrewtest(Model model, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
                              @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("GetMapping findAllCrew");
        log.info("crewSportRequest : {}", crewSportRequest.getStrict());

        List<String> sportsList = crewSportRequest.getSportsList();

        if (CollectionUtils.isEmpty(sportsList))
            log.info("list empty");
        else{
            for( String s : sportsList){
                log.info(s);
            }
        }

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

        return "main/test";
    }

    @GetMapping("/test2")
    public void multi(@RequestParam List<String> checkedValue){
        for (String c : checkedValue) {
            log.info(c);
        }
    }


//        String[] listArr = list.split("/");
//
//        for(int i = 0 ; i < listArr.length; i++) {
//            BoardVO board = service.get((long)Integer.parseInt(listArr[i]));
//            System.out.println(board);
//        }
//    }


//    @PostMapping("/test")
//    public String findAllCrewtest3(Model model, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
//                                  @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        log.info("PostMapping findAllCrew");
//        log.info("crewSportRequest : {}", crewSportRequest.getStrict());
//
//        List<String> sportsList = crewSportRequest.getSportsList();
//
//        if (CollectionUtils.isEmpty(sportsList))
//            log.info("list empty");
//        else{
//            for( String s : sportsList){
//                log.info(s);
//            }
//        }
//
//
//        Page<CrewDetailResponse> list;
//        if(crewSportRequest.getStrict() == null)
//            list = crewService.findAllCrews(pageable);
//        else if(crewSportRequest.getStrict() != null)
//            list = crewService.findAllCrewsWithStrict(crewSportRequest, pageable);
//        else
//            list = crewService.findAllCrewsBySport(crewSportRequest, pageable);
//
//
//        // 페이징 처리 변수
//        int nowPage = list.getPageable().getPageNumber() + 1;
//        int startPage = Math.max(nowPage - 4, 1);
//        int endPage = Math.min(nowPage + 5, list.getTotalPages());
//        int lastPage = list.getTotalPages();
//
//        // 게시글 리스트
//        model.addAttribute("crewList", list);
//
//        // 페이징 처리 모델
//        model.addAttribute("nowPage", nowPage);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//        model.addAttribute("lastPage", lastPage);
//
//
//        //test : 종목 검색
//        model.addAttribute("sportRequest",  crewSportRequest);
//
//        return "main/test";
//    }







}
