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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
<<<<<<< src/main/java/teamproject/pocoapoco/controller/main/ui/CrewViewController.java
import teamproject.pocoapoco.domain.dto.crew.*;
import teamproject.pocoapoco.domain.entity.User;
=======
import teamproject.pocoapoco.domain.dto.crew.CrewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewResponse;
import teamproject.pocoapoco.domain.dto.crew.CrewSportRequest;
>>>>>>> src/main/java/teamproject/pocoapoco/controller/main/ui/CrewViewController.java
import teamproject.pocoapoco.domain.dto.crew.members.CrewMemberDeleteResponse;
import teamproject.pocoapoco.domain.dto.crew.members.CrewMemberResponse;
import teamproject.pocoapoco.domain.dto.like.LikeViewResponse;
import teamproject.pocoapoco.domain.dto.user.UserProfileResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.CrewMemberService;
import teamproject.pocoapoco.service.CrewService;
import teamproject.pocoapoco.service.LikeViewService;

import javax.persistence.EntityNotFoundException;
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
    private final LikeViewService likeViewService;
    private final CrewMemberService crewMemberService;
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;

    // 크루 게시물 상세 페이지
    @GetMapping("/{crewId}")
<<<<<<< src/main/java/teamproject/pocoapoco/controller/main/ui/CrewViewController.java
    public String detailCrew(@PathVariable Long crewId, Model model, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
                             @PageableDefault(page = 0, size = 1, sort = "lastModifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // 크루 게시물 검색 필터(전체조회, 지역조회, 운동종목 조회)
        Page<CrewDetailResponse> list = crewService.findAllCrewsByStrictAndSportEnum(crewSportRequest, true, pageable);

        try {
            CrewDetailResponse details = list.getContent().get(0);
            //알림 체크
            crewService.readAlarms(crewId, authentication.getName());
=======
    public String detailCrew(@PathVariable Long crewId, Model model, Authentication authentication) {
        try {
            

            CrewDetailResponse details = crewService.detailCrew(crewId);
>>>>>>> src/main/java/teamproject/pocoapoco/controller/main/ui/CrewViewController.java
            int count = likeViewService.getLikeCrew(crewId);

            model.addAttribute("details", details);
            model.addAttribute("likeCnt", count);
        } catch (EntityNotFoundException e) {
            return "redirect:/index";
        }

        // 페이징 처리 변수
        int nowPage = list.getPageable().getPageNumber();
        int lastPage = list.getTotalPages() - 1;

        // 페이징 처리 모델
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("lastPage", lastPage);

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

        return "redirect:" + "/view/v1/crews/" + crewResponse.getCrewId();
    }

    // 크루 게시글 수정화면
    @GetMapping("/update/{crewId}")
    public String updateCrew(@PathVariable Long crewId, Model model, Authentication authentication) {
        Crew crew = crewRepository.findById(crewId).orElse(null);
        if (crew == null || !crew.getUser().getUsername().equals(authentication.getName())) {
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
    public String deleteCrew(@PathVariable Long crewId, Model model, Authentication authentication) {
        Crew crew = crewRepository.findById(crewId).orElse(null);
        User user = userRepository.findById(crew.getUser().getId()).orElse(null);
        log.info("삭제 조회 중");

        if (crew == null || user == null) {
            return "error/404";
        }
        CrewResponse crewResponse = crewService.deleteCrew(crewId, authentication.getName());
        model.addAttribute("response", crewResponse);
        return "redirect:/";
    }

    // 크루 좋아요 누르기
    @PostMapping("/{crewId}/like")
    public ResponseEntity likeCrew(@PathVariable Long crewId, Authentication authentication) {
        LikeViewResponse likeViewResponse = likeViewService.pressLike(crewId, authentication.getName());
        return new ResponseEntity<>(likeViewResponse, HttpStatus.OK);
    }

    // 크루 참여한 MemberList 출력
    @GetMapping("/{crewId}/joinCrew")
    public ResponseEntity getJoinList(@PathVariable Long crewId) {
        List<CrewMemberResponse> list = crewMemberService.getJoinMemberList(crewId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 크루 참여하기
    @PostMapping("/{crewId}/joinCrew")
    public ResponseEntity joinCrew(@PathVariable Long crewId, Authentication authentication) {
        CrewMemberResponse joinMemberResponse = crewMemberService.joinCrew(crewId, authentication.getName());
        return new ResponseEntity<>(joinMemberResponse, HttpStatus.OK);
    }

    // 크루 나가기
    @DeleteMapping("/{crewId}/leaveCrew")
    public ResponseEntity leaveCrew(@PathVariable Long crewId, Authentication authentication) {
        CrewMemberDeleteResponse crewMemberDeleteResponse = crewMemberService.leaveCrew(crewId, authentication.getName());
        return new ResponseEntity<>(crewMemberDeleteResponse, HttpStatus.OK);
    }


    // 크루 게시물 전체 조회, 검색 조회, 운동 종목 조회
    @GetMapping()
    @ApiOperation(value = "크루 게시글 전체조회", notes = "")
    public String findAllCrew(Model model, Authentication authentication,
                              @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
                              @PageableDefault(page = 0, size = 9, sort = "lastModifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // 유저 로그인 확인 후 운동 종목 데이터 확인
        List<String> userSportsList = crewService.getUserSports(authentication, CollectionUtils.isEmpty(crewSportRequest.getSportsList()));

        // 처음 로그인 시, My운동종목 클릭 시 유저 종목 Model에 추가
        if (!CollectionUtils.isEmpty(userSportsList) && !crewSportRequest.isLoginStatus()) {
            crewSportRequest.setSportsList(userSportsList);
            crewSportRequest.setLoginStatus(true);
        }

        // 크루 게시물 검색 필터(전체조회, 지역조회, 운동종목 조회)
        Page<CrewDetailResponse> list = crewService.findAllCrewsByStrictAndSportEnum(crewSportRequest, CollectionUtils.isEmpty(userSportsList), pageable);

        // 페이징 처리 변수
        int nowPage = list.getPageable().getPageNumber() + 1;
        int startNumPage = Math.max(nowPage - 4, 1);
        int endNumPage = Math.min(nowPage + 5, list.getTotalPages());
        int lastPage = list.getTotalPages();

        // 게시글 리스트
        model.addAttribute("crewList", list);

        // 페이징 처리 모델
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startNumPage", startNumPage);
        model.addAttribute("endNumPage", endNumPage);
        model.addAttribute("lastPage", lastPage);

        return "main/main";
    }

//    @GetMapping("/{crewId}")
//    public String detailCrew(@PathVariable Long crewId, Model model,
//                             @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
//                             @PageableDefault(page = 0, size = 1, sort = "lastModifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        // 크루 게시물 검색 필터(전체조회, 지역조회, 운동종목 조회)
//        Page<CrewDetailResponse> list = crewService.findAllCrewsByStrictAndSportEnum(crewSportRequest, true, pageable);
//
//        try {
//            CrewDetailResponse details = list.getContent().get(0);
//            int count = likeViewService.getLikeCrew(crewId);
//
//            model.addAttribute("details", details);
//            model.addAttribute("likeCnt", count);
//        } catch (EntityNotFoundException e) {
//            return "redirect:/index";
//        }
//
//        // 페이징 처리 변수
//        int nowPage = list.getPageable().getPageNumber();
//        int lastPage = list.getTotalPages() - 1;
//
//        // 페이징 처리 모델
//        model.addAttribute("nowPage", nowPage);
//        model.addAttribute("lastPage", lastPage);
//
//        return "crew/read-crew";
//    }

    @ModelAttribute("sportEnums")
    private List<SportEnum> sportEnums() {
        List<SportEnum> sportEnums = List.of(SportEnum.values());
        return sportEnums;
    }

}
