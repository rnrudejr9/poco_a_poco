package teamproject.pocoapoco.service;

import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EncrypterConfig encrypterConfig;

    public UserService(UserRepository userRepository, EncrypterConfig encrypterConfig) {
        this.userRepository = userRepository;
        this.encrypterConfig = encrypterConfig;
    }

    // 토큰 까는 method
    public User getUserByUserName(String userName) {

        Optional<User> userOptional = userRepository.findByUserName(userName);

        if(userOptional.isEmpty()){
            throw new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }

        return userOptional.get();
    }


    public UserJoinResponse addUser(UserJoinRequest request){

        String encodedPassword = encrypterConfig.encoder().encode(request.getPassword());


        if (userRepository.findByUserId(request.getUserId()).isPresent()){
            throw new AppException(ErrorCode.DUPLICATED_USERID, ErrorCode.DUPLICATED_USERID.getMessage());

        } // 아이디 중복 확인 버튼 생성?

        if (userRepository.findByUserName(request.getUserName()).isPresent()){
            throw new AppException(ErrorCode.DUPLICATED_USERNAME, ErrorCode.DUPLICATED_USERNAME.getMessage());

        }


        User user;

        if(request.getLikeSoccer()){
             user = User.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .address(request.getAddress())
                    .role(UserRole.ROLE_USER)
                    .address(request.getAddress())
                    .sport(InterestSport.SOCCER)
                    .password(encodedPassword)
                    .build();



        } else if (request.getLikeJogging()) {
            user = User.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .address(request.getAddress())
                    .role(UserRole.ROLE_USER)
                    .address(request.getAddress())
                    .sport(InterestSport.JOGGING)
                    .password(encodedPassword)
                    .build();


        } else if (request.getLikeTennis()) {
            user = User.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .address(request.getAddress())
                    .role(UserRole.ROLE_USER)
                    .address(request.getAddress())
                    .sport(InterestSport.TENNIS)
                    .password(encodedPassword)
                    .build();



        } else {

            user = User.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .address(request.getAddress())
                    .role(UserRole.ROLE_USER)
                    .address(request.getAddress())
                    .password(encodedPassword)
                    .build();


        }

        User saved = userRepository.save(user);


        UserJoinResponse response = new UserJoinResponse(saved.getUserId(), "회원가입 되었습니다.");

        return response;


    }



}
