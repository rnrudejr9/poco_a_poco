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
import teamproject.pocoapoco.domain.dto.crew.review.CrewReviewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.review.CrewReviewResponse;
import teamproject.pocoapoco.domain.dto.like.LikeViewResponse;
import teamproject.pocoapoco.domain.dto.part.PartJoinResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.SportEnum;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.*;
import teamproject.pocoapoco.service.part.ParticipationService;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/v1/crews")
@Slf4j
@Api(tags = {"Crew Controller"})
public class CrewViewController {

    private final CrewService crewService;
    private final LikeViewService likeViewService;
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;
    private final CrewReviewService crewReviewService;

    private final ParticipationService participationService;
    /*@ModelAttribute("reviews")
    public Map<String, String> reviews() {
        Map<String, String> reviews = new LinkedHashMap<>();
        reviews.put("01", "시간을 잘 지켜요.");
        reviews.put("02", "다음 모임에서도 함께하고 싶어요.");
        return reviews;
    }*/


    // 크루 게시물 상세 페이지
    @GetMapping("/{crewId}")
    public String detailCrew(@PathVariable Long crewId, Model model, @ModelAttribute("sportRequest") CrewSportRequest crewSportRequest,
                             @PageableDefault(page = 0, size = 1, sort = "lastModifiedAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {

        // 크루 게시물 검색 필터(전체조회, 지역조회, 운동종목 조회)
        Page<CrewDetailResponse> list = crewService.findAllCrewsByStrictAndSportEnum(crewSportRequest, true, pageable);

        try {
            CrewDetailResponse details = list.getContent().get(0);
            //알림 체크
            if(authentication != null) crewService.readAlarms(crewId, authentication.getName());
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

    // 크루 리뷰 작성
    @GetMapping("/review/{crewId}")
    public String reviewCrew(@PathVariable Long crewId, Authentication authentication, Model model) {

        //현재 유저
        User nowUser = crewService.findByUserName(authentication.getName());
        model.addAttribute("nowUser", nowUser.getId());

        // 크루 id, title. userId
        Crew crew = crewService.findByCrewId(crewId);
        model.addAttribute("crew", crew);

        List<PartJoinResponse> partJoinResponses = participationService.AllowedMember(crewId);
        Deque<User> members = new ArrayDeque<>();
        User u;
        for (PartJoinResponse p : partJoinResponses){

            log.info("!!p.getCrewId() : {}, p.getNow() : {}, p.getJoinUserName() : {}, p.getWriterUserName() : {}", p.getCrewId(), p.getNow(), p.getJoinUserName(), p.getWriterUserName());
            u =crewService.findByUserName(p.getJoinUserName());
            if(crew.getUser().getId() == u.getId())
                members.addFirst(u);
            else
                members.add(u);
        }


        // 유저 id, nicname, mannaerScore
        model.addAttribute("members", members);


        CrewReviewRequest crewReviewRequest = new CrewReviewRequest();
        model.addAttribute("reviewRequest", crewReviewRequest);

        return "crew/review-crew";
    }


    // 크루 리뷰 완료
    @PostMapping("/review")
    public String reviewCrew(Model model,
                             @ModelAttribute("reviewRequest") CrewReviewRequest crewReviewRequest) {
        log.info("post review");

        log.info("CrewId().size() : {}", crewReviewRequest.getCrewId().size());
        log.info("CrewId() : {}", crewReviewRequest.getCrewId());

        log.info("FromUserId().size() : {}", crewReviewRequest.getFromUserId().size());
        log.info("FromUserId() : {}", crewReviewRequest.getFromUserId());

        log.info("ToUserId().size() : {}", crewReviewRequest.getToUserId().size());
        log.info("ToUserId().get(0) : {}\n", crewReviewRequest.getToUserId());

        log.info("MannerScore().size() : {}", crewReviewRequest.getMannerScore().size());
        log.info("MannerScore().get(0) : {}\n", crewReviewRequest.getMannerScore());

        log.info("UserReview().size() : {}", crewReviewRequest.getUserReview().size());
        log.info("UserReview().get() : {}\n", crewReviewRequest.getUserReview());

        crewReviewService.addReview(crewReviewRequest);

        return "redirect:/";
    }

    @GetMapping("/reviewList")
    public String inquireReviewList(Authentication authentication, Model model) {
        String userName = authentication.getName();

        List<CrewReviewResponse> reviewList = crewReviewService.inquireAllReviewList(userName);
        model.addAttribute("reviewList", reviewList);

        long reviewAllCount = crewReviewService.getReviewAllCount(userName);
        model.addAttribute("reviewAllCount", reviewAllCount);

//        model.addAttribute("certifyYn", receiver.getCertifyYn());
//        model.addAttribute("nickname", receiver.getNickname());

        return "review/review-list";
    }
    @GetMapping("/reviewList/{reviewId}")
    public String inquireReview(@PathVariable Long reviewId, Model model) {

        CrewReviewDetailResponse review = crewReviewService.inquireReview(reviewId);
        model.addAttribute("review", review);

        return "review/review-content";
    }

    @ModelAttribute("sportEnums")
    private List<SportEnum> sportEnums() {
        List<SportEnum> sportEnums = List.of(SportEnum.values());
        return sportEnums;
    }


    // 내가 참여중인 모임 리스트
    @GetMapping("/users/activeCrew")
    public String getActiveCrewList(Authentication authentication, Model model) {

        try{
            String userName = authentication.getName();
            // list
            List<CrewDetailResponse> crewList = crewService.inquireAllCrew(2,authentication.getName()); // 2: 참여 완료
            model.addAttribute("crewList",crewList);

            // count
            putCategorizeCrewCount(userName,model);
            return "part/get-current-crew";
        } catch (AppException e){
            return "redirect:/view/v1/start";
        }
    }
    // 내가 참여했고 종료된 모임 리스트
    @GetMapping("/users/endCrew")
    public String getEndCrewList(Authentication authentication, Model model) {

        try{
            String userName = authentication.getName();
            // list
            List<CrewDetailResponse> crewList = crewService.inquireAllCrew(3,authentication.getName()); // 3: 모집 종료
            model.addAttribute("crewList",crewList);

            // count
            putCategorizeCrewCount(userName,model);

            return "part/get-end-crew";
        } catch (AppException e){
            return "redirect:/view/v1/start";
        }
    }

    private void putCategorizeCrewCount(String userName, Model model) {
        long activeCrewCount = crewService.getCrewByUserAndStatus(2,userName);
        long endCrewCount = crewService.getCrewByUserAndStatus(3,userName);
        model.addAttribute("activeCrewCount", activeCrewCount);
        model.addAttribute("endCrewCount", endCrewCount);

    }


}