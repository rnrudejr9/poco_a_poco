package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.comment.CommentResponse;
import teamproject.pocoapoco.domain.dto.follow.FollowingResponse;
import teamproject.pocoapoco.domain.entity.Comment;
import teamproject.pocoapoco.domain.entity.Follow;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.FollowRepository;
import teamproject.pocoapoco.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public FollowingResponse follow(String followingUserId, Long userId){

        User followingUser = userRepository.findByUserName(followingUserId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });

        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        //user 와 login한 user의 id가 같은경우
        if(userId.equals(followingUserId)){
            throw new AppException(ErrorCode.WRONG_PATH,ErrorCode.WRONG_PATH.getMessage());
        }

        Optional<Follow> follow = followRepository.findByFollowingUserIdAndFollowedUserId(followingUser.getId(), user.getId());

        if(followRepository.findByFollowingUserIdAndFollowedUserId(followingUser.getId(),user.getId()).isPresent()){
            followRepository.delete(follow.get());
            //팔로우 취소
            return new FollowingResponse(user.getUsername(),user.getNickName(),false);
        }else
            //팔로우
            followRepository.save(new Follow(followingUser,user));
        return new FollowingResponse(user.getUsername(),user.getNickName(),true);

    }
//    public String unFollow(String unFollowingUserId, Long userId){
//
//        User unFollowingUser = userRepository.findByUserId(unFollowingUserId).orElseThrow(()->
//        {
//            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
//        });
//
//        User user = userRepository.findById(userId).orElseThrow(()->
//        {
//            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
//        });
//
//        if(userId.equals(unFollowingUserId)){
//            throw new AppException(ErrorCode.WRONG_PATH,"자기 자신을 팔로우 취소할 수 없습니다.");
//        }
//
//        Follow follow = followRepository.findByFollowingUserIdAndFollowedUserId(unFollowingUser.getId(),user.getId()).orElseThrow(()->
//        {
//            throw new AppException(ErrorCode.WRONG_PATH,"해당 유저를 팔로우 하고 있지 않습니다");
//
//        });
//        followRepository.delete(follow);
//        return user.getUsername()+"님을 팔로우 취소합니다.";
//
//    }

    @Transactional
    public Integer followedCount(Long userId){ //해당 유저를 팔로우 하고 있는 유저의 수
        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        return followRepository.countByFollowedUserId(userId);
    }
    @Transactional
    public Integer followingCount(Long userId){ //해당 유자가 팔로잉 하고 있는 유저의 수
        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        return followRepository.countByFollowingUserId(userId);
    }

    public Page<FollowingResponse> getFollowingList(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        Page<Follow> list = followRepository.findByFollowingUserId(pageable,userId);
        return list.map(FollowingResponse::followingResponse);
    }
    public Page<FollowingResponse> getFollowedList(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        Page<Follow> list = followRepository.findByFollowedUserId(pageable,userId);
        return list.map(FollowingResponse::followedResponse);
    }

}
