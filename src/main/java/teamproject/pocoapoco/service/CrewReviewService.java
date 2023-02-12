package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.Review.ReviewRequest;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Review;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.CrewReviewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.service.part.ParticipationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

}
