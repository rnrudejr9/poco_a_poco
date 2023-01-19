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
    public String follow(String followingUserId, String userId){
        User followingUser = userRepository.findByUserId(followingUserId).orElseThrow(()->
        {throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });

        User user = userRepository.findByUserId(userId).orElseThrow(()->
        {throw new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage());
        });
        if(userId.equals(followingUserId)){
            throw new AppException(ErrorCode.WRONG_PATH,"자기 자신을 팔로우 할 수 없습니다.");
        }

        if(followRepository.findByFollowingUserIdAndFollowedUserId(followingUser.getId(),user.getId()).isPresent()){
            throw new AppException(ErrorCode.WRONG_PATH,"이미 해당유저를 팔로우 하고 있습니다.");
        }else
            followRepository.save(new Follow(followingUser,user));
        return user.getUsername()+"님을 팔로우 합니다.";


    }


}
