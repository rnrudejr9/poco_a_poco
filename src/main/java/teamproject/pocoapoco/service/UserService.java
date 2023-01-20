package teamproject.pocoapoco.service;

import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.domain.user.*;
import teamproject.pocoapoco.enums.InterestSport;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;
import teamproject.pocoapoco.security.provider.JwtProvider;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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
                encrypterConfig.encoder().encode(userJoinRequest.getPassword()), userJoinRequest.getLikeSoccer(),
                userJoinRequest.getLikeJogging(), userJoinRequest.getLikeTennis());

        User saved = userRepository.save(user);


        UserJoinResponse userJoinResponse = new UserJoinResponse(saved.getUserId(), "회원가입 되었습니다.");

        return userJoinResponse;


    }

    @Transactional(rollbackOn = AppException.class)
    public UserProfileResponse modifyMyUserInfo(String token, UserProfileRequest userProfileRequest) {

        // 비밀번호 확인 로직 따로 빼야할 필요 있음
        if (!userProfileRequest.getPassword().equals(userProfileRequest.getPasswordConfirm())){
            throw new AppException(ErrorCode.NOT_MATCH_PASSWORD, ErrorCode.NOT_MATCH_PASSWORD.getMessage());
        }

        JwtProvider jwtProvider = new JwtProvider();

        // token이 valid한지 확인
        if(!jwtProvider.validateToken(token)){
            throw new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }

        String userId = jwtProvider.getUserId(token);

        // user id 확인
        Optional<User> myUserOptional = userRepository.findByUserId(userId);

        if(myUserOptional.isEmpty()){
            throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        User beforeMyUser = myUserOptional.get();

        // request에서 수정된 정보만 반영하기

        String userName = (userProfileRequest.getUserName().equals(null))? beforeMyUser.getUsername(): userProfileRequest.getUserName();
        String address = (userProfileRequest.getAddress().equals(null))? beforeMyUser.getAddress(): userProfileRequest.getAddress();
        String password = (userProfileRequest.getPassword().equals(null))? beforeMyUser.getPassword(): userProfileRequest.getPassword();
        Boolean likeSoccer = (userProfileRequest.getLikeSoccer().equals(beforeMyUser.getSport().isSoccer()))? beforeMyUser.getSport().isSoccer(): userProfileRequest.getLikeSoccer();
        Boolean likeJogging = (userProfileRequest.getLikeJogging().equals(beforeMyUser.getSport().isJogging()))? beforeMyUser.getSport().isJogging(): userProfileRequest.getLikeJogging();
        Boolean likeTennis = (userProfileRequest.getLikeTennis().equals(beforeMyUser.getSport().isTennis()))? beforeMyUser.getSport().isTennis(): userProfileRequest.getLikeTennis();


        User revisedMyUser = User.toEntity(beforeMyUser.getUserId(), userName, address, password, likeSoccer, likeJogging, likeTennis);

        userRepository.save(revisedMyUser);

        return UserProfileResponse.fromEntity(revisedMyUser);

    }

    public UserProfileResponse selectUserInfo(Long id) {

       Optional<User> selectedUserOptional = userRepository.findById(id);

       if(selectedUserOptional.isEmpty()){
           throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
       }

       User selectedUser = selectedUserOptional.get();

        return UserProfileResponse.fromEntity(selectedUser);

    }


//    public UserProfileResponse selectMyFollower() {
//    }
//
//    public UserProfileResponse selectMyFollowing() {
//    }


}

