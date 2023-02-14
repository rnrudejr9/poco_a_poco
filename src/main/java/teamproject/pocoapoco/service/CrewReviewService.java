package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.Review.ReviewRequest;
import teamproject.pocoapoco.domain.dto.crew.review.CrewReviewDetailResponse;
import teamproject.pocoapoco.domain.dto.crew.review.CrewReviewResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.Review;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.entity.part.Participation;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.CrewReviewRepository;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.repository.part.ParticipationRepository;

import javax.transaction.Transactional;
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
    private final ParticipationRepository participationRepository;

    // 리뷰 저장
    @Transactional
    public void addReview(ReviewRequest crewReviewRequest) {

        List<Review> reviewList = new ArrayList<>();

        try{
            Crew crew = crewRepository.findById(crewReviewRequest.getCrewId().get(0)).get();
            User fromUser = userRepository.findById(crewReviewRequest.getFromUserId().get(0)).get();

            for (int i = 0; i < crewReviewRequest.getCrewId().size(); i++) {
                Review review = new Review();

                User toUser = userRepository.findById(crewReviewRequest.getToUserId().get(i)).get();

                review.of(crew, fromUser, toUser,
                        crewReviewRequest.getUserMannerScore().get(i), crewReviewRequest.getUserReview().get(i));
                reviewList.add(review);
            }
            crewReviewRepository.saveAll(reviewList);

            // reviewScore 저장
            for (int i = 0; i < crewReviewRequest.getCrewId().size(); i++) {
                User toUser = userRepository.findById(crewReviewRequest.getToUserId().get(i)).get();
                Review review = crewReviewRepository.findReviewByCrewAndToUser(crew, toUser);
                toUser.addReviewScore(review.getReviewScore());
            }

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

    public Page<CrewReviewResponse> findAllReviewList(String userName, Pageable pageable) {
        User ToUser = userRepository.findByUserName(userName).get();
        Page<Review> allReviewList = crewReviewRepository.findByToUser(ToUser, pageable);
        return allReviewList.map(CrewReviewResponse::of);
    }

    // 리뷰 detail
    public CrewReviewDetailResponse findReviewById(Long reviewId) {
        Review review = crewReviewRepository.findById(reviewId).get();



        return CrewReviewDetailResponse.builder()
                .id(reviewId)
                .fromUserName(review.getFromUser().getNickName())
                .crewTitle(review.getCrew().getTitle())
                .reviewContext(review.getReviewContext())
//                .reviews(reviews)
                .build();
    }
/*
    // reviewScore 계산
    @Transactional
    public double calcReviewScore(Long crewId, User receiver) {
        // review 받는 사람 list Participation에서 구하고
        Crew crew = crewRepository.findById(crewId).get();
        // 같이 모임을 한 사람들의 review에서 reviewScore만 추출
        List<Double> reviewScores = crewReviewRepository.findReviewByCrewAndToUser(crew, receiver)
                .stream()
                .map(Review::getReviewScore)
                .collect(Collectors.toList());
        double reviewScoreToUser = reviewScores.stream().mapToDouble(i -> i).sum();
        System.out.println("receiver:"+receiver.getUsername()+"받을 점수"+reviewScoreToUser);
        int reviewCount = crewReviewRepository.countReviewByCrewAndToUser(crew, receiver);
        double result = Math.round(reviewScoreToUser / reviewCount);

        return (result*100) / 100.0; // 소수점 2째자리

    }
         */

    // reviewScore 저장
    /*@Transactional
    public void addMannerScore(Long crewId) {
        Crew crew = crewRepository.findById(crewId).get();
        // 해당 모임에 참여한 참가자에서 user을 추출
        List<User> users = participationRepository.findByCrew(crew)
                .stream()
                .map(Participation::getUser)
                .collect(Collectors.toList());
        for(User user : users) {
            // user의 mannerScore에 계산된 reviewScore 추가
             Review review = crewReviewRepository.findReviewByCrewAndToUser(crew, user);
            double score = review.getReviewScore();
            System.out.println("receiver:"+user.getUsername()+"받을 점수"+score);
            user.addReviewScore(score);
        }
    }*/


    public long getReviewAllCount(String userName) {
        User user = userRepository.findByUserName(userName).get();
        return crewReviewRepository.countReviewByToUser(user);
    }


}
