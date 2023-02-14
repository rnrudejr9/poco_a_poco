package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.Review.ReviewRequest;
import teamproject.pocoapoco.domain.dto.crew.review.CrewReviewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.review.CrewReviewResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Review;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.CrewReviewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrewReviewService {
    private final UserRepository userRepository;
    private final CrewRepository crewRepository;

    private final CrewReviewRepository crewReviewRepository;

    // 리뷰 저장
    public void addReview(ReviewRequest crewReviewRequest) {

        List<Review> reviewList = new ArrayList<>();

        try{
            Optional<Crew> crew = crewRepository.findById(crewReviewRequest.getCrewId().get(0));
            Optional<User> fromUser = userRepository.findById(crewReviewRequest.getFromUserId().get(0));

            for (int i = 0; i < crewReviewRequest.getCrewId().size(); i++) {
                Review review = new Review();

                Optional<User> toUser = userRepository.findById(crewReviewRequest.getToUserId().get(i));

                review.of(crew.get(), fromUser.get(), toUser.get(),
                        crewReviewRequest.getUserMannerScore().get(i), crewReviewRequest.getUserReview().get(i));
                reviewList.add(review);
            }
            crewReviewRepository.saveAll(reviewList);
        }catch (NullPointerException e){
            log.info("이용자 후기 NullPointerException : 작성 가능한 후기 내용이 없습니다.");
        }
    }

    // 리뷰 작성 여부 확인
    public boolean findReviewedUser(Long crewId, User nowUser) {

        List<Review> reviewList = crewReviewRepository.findByCrewId(crewId);

        for(Review r : reviewList){
            if(r.getFromUser().getId() == nowUser.getId())
                return true;
        }
        return false;
    }

    public List<CrewReviewResponse> inquireAllReviewList(String userName) {
        User ToUser = userRepository.findByUserName(userName).get();
        List<Review> allReviewList = crewReviewRepository.findByToUser(ToUser);
        return allReviewList.stream()
                .map(review -> CrewReviewResponse.builder()
                        .id(review.getId())
                        .fromUserName(review.getFromUser().getNickName())
                        .crewTitle(review.getCrew().getTitle())
                        .crewImage(review.getCrew().getImagePath())
                        .build())
                .collect(Collectors.toList());
    }
    // 리뷰 detail
    public CrewReviewDetailResponse inquireReview(Long reviewId) {
        Review review = crewReviewRepository.findById(reviewId).get();

        /*LinkedHashMap<String, String> reviews = new LinkedHashMap<>();
        String[] reviewValues = review.getReviews().split("\\|");
        for (String reviewValue : reviewValues) {
            switch (reviewValue) {
                case "01":
                    reviews.put("01", "시간을 잘 지켜요.");
                    break;
                case "02":
                    reviews.put("02", "다음 모임에서도 함께하고 싶어요.");
                    break;
            }

        }*/

        return CrewReviewDetailResponse.builder()
                .id(reviewId)
                .fromUserName(review.getFromUser().getNickName())
                .crewTitle(review.getCrew().getTitle())
                .reviewContext(review.getReviewContext())
//                .reviews(reviews)
                .build();
    }
//
//    public double calcReviewScore


    public long getReviewAllCount(String userName) {
        User user = userRepository.findByUserName(userName).get();
        return crewReviewRepository.countReviewByToUser(user);
    }


}
