package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.entity.Follow;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.FollowRepository;
import teamproject.pocoapoco.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    public String follow(String followingUserId, Long userId){

        User followingUser = userRepository.findByUserId(followingUserId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });

        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        //user 와 login한 user의 id가 같은경우
        if(userId.equals(followingUserId)){
            throw new AppException(ErrorCode.WRONG_PATH,"자기 자신을 팔로우 할 수 없습니다.");
        }

        if(followRepository.findByFollowingUserIdAndFollowedUserId(followingUser.getId(),user.getId()).isPresent()){
            throw new AppException(ErrorCode.WRONG_PATH,"이미 해당유저를 팔로우 하고 있습니다.");
        }else
            followRepository.save(new Follow(followingUser,user));
        return user.getUsername()+"님을 팔로우 합니다.";

    }
    public String unFollow(String unFollowingUserId, Long userId){

        User unFollowingUser = userRepository.findByUserId(unFollowingUserId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });

        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });

        if(userId.equals(unFollowingUserId)){
            throw new AppException(ErrorCode.WRONG_PATH,"자기 자신을 팔로우 취소할 수 없습니다.");
        }

        Follow follow = followRepository.findByFollowingUserIdAndFollowedUserId(unFollowingUser.getId(),user.getId()).orElseThrow(()->
        {
            throw new AppException(ErrorCode.WRONG_PATH,"해당 유저를 팔로우 하고 있지 않습니다");

        });
        followRepository.delete(follow);
        return user.getUsername()+"님을 팔로우 취소합니다.";

    }
    public Integer followedCount(Long userId){ //해당 유저를 팔로우 하고 있는 유저의 수
        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        return followRepository.countByFollowedUserId(userId);
    }
    public Integer followingCount(Long userId){ //해당 유자가 팔로잉 하고 있는 유저의 수
        User user = userRepository.findById(userId).orElseThrow(()->
        {
            throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        return followRepository.countByFollowingUserId(userId);
    }
}
