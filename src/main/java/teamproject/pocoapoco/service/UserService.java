package teamproject.pocoapoco.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.UserJoinRequest;
import teamproject.pocoapoco.domain.user.UserJoinResponse;
import teamproject.pocoapoco.domain.user.UserLoginRequest;
import teamproject.pocoapoco.domain.user.UserLoginResponse;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;
import teamproject.pocoapoco.security.provider.JwtProvider;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EncrypterConfig encrypterConfig;
    private final UserRepository userRepository;



    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        // userName 유효성 확인
        User user = userRepository.findByUserId(userLoginRequest.getUserId())
                .orElseThrow(() -> {throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
                });

        // password 유효성 확인
        if (!encrypterConfig.encoder().matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());
        }

        //token 발행
        return new UserLoginResponse(new JwtProvider().generateToken(user));
    }



    public UserJoinResponse addUser(UserJoinRequest userJoinRequest){

        String encodedPassword = encrypterConfig.encoder().encode(userJoinRequest.getPassword());

        // 비밀번호 확인 로직 추가

        if (!userJoinRequest.getPassword().equals(userJoinRequest.getPasswordConfirm())){

            throw new AppException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());
        }


        if (userRepository.findByUserId(userJoinRequest.getUserId()).isPresent()){
            throw new AppException(ErrorCode.DUPLICATED_USERID, ErrorCode.DUPLICATED_USERID.getMessage());

        } // 아이디 중복 확인 버튼 생성?

        if (userRepository.findByUserName(userJoinRequest.getUserName()).isPresent()){
            throw new AppException(ErrorCode.DUPLICATED_USERNAME, ErrorCode.DUPLICATED_USERNAME.getMessage());

        }


        User user = User.toEntity(userJoinRequest.getUserId(), userJoinRequest.getUserName(), userJoinRequest.getAddress(),
                userJoinRequest.getPassword(), userJoinRequest.getLikeSoccer(),
                userJoinRequest.getLikeJogging(), userJoinRequest.getLikeTennis());

        User saved = userRepository.save(user);


        UserJoinResponse userJoinResponse = new UserJoinResponse(saved.getUserId(), "회원가입 되었습니다.");

        return userJoinResponse;


    }


}

