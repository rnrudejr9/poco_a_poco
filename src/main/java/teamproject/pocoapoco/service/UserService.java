package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import teamproject.pocoapoco.domain.dto.user.*;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;
import teamproject.pocoapoco.security.provider.JwtProvider;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EncrypterConfig encrypterConfig;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;


    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        // userName 유효성 확인
        User user = userRepository.findByUserId(userLoginRequest.getUserId())
                .orElseThrow(() -> {throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
                });
        // password 유효성 확인
        if (!encrypterConfig.encoder().matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());
        }
        // refresh token 생성
        String refreshToken = jwtProvider.generateRefreshToken(user);
        // Redis에 저장 - 만료 시간 설정을 통해 자동 삭제 처리
        redisTemplate.opsForValue().set(
                user.getUsername(),
                refreshToken,
                jwtProvider.REFRESH_EXPIRATION,
                TimeUnit.MILLISECONDS
        );

        //token 발행
        return new UserLoginResponse(refreshToken, new JwtProvider().generateAccessToken(user));
    }

    public UserJoinResponse saveUser(UserJoinRequest userJoinRequest){

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

    public ReIssueResponse regenerateToken(ReIssueRequest reIssueRequest) {
        String currentRefreshToken = reIssueRequest.getRefreshToken();
        String currentAccessToken = reIssueRequest.getAccessToken();

        // Refresh Token 검증
        if (!jwtProvider.validateToken(currentRefreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Refresh Token 정보가 유효하지 않습니다");
        }

        // Access Token 에서 Username 출력
        Authentication authentication = jwtProvider.getAuthentication(currentAccessToken);
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow(() -> {
            throw new AppException(ErrorCode.USERID_NOT_FOUND, "아이디가 존재하지 않습니다.");
        });

        // Redis 에서 UserName 을 기반으로 저장된 Refresh Token 값 호출
        String refreshToken = redisTemplate.opsForValue().get(authentication.getName());
        // 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new AppException(ErrorCode.EXPIRED_TOKEN, "Refresh Token이 없습니다.");
        }
        if (!refreshToken.equals(reIssueRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Refresh Token 정보가 일치하지 않습니다."); // refresh token이 일치하지 않음.
        }

        // 새로운 토큰 생성
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        Long expiredTime = jwtProvider.getCurrentExpiration(currentRefreshToken);

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set(authentication.getName(), newRefreshToken, expiredTime , TimeUnit.MILLISECONDS);

        return new ReIssueResponse(refreshToken, new JwtProvider().generateAccessToken(user));
    }


    public UserLogoutResponse logout(UserLogoutRequest userLogoutRequest) {
        if(!jwtProvider.validateToken(userLogoutRequest.getAccessToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
        // AccessToken에서 userName 추출
        Authentication authentication = jwtProvider.getAuthentication(userLogoutRequest.getAccessToken());

        // refresh token이 남아있다면 삭제
        if(redisTemplate.opsForValue().get(authentication.getName())!= null) {
            redisTemplate.delete(authentication.getName());
        }
        // 해당 refeshtoken의 accesstoken의 유효시간을 가져와 key에 저장
        Long expiredTime = jwtProvider.getCurrentExpiration(userLogoutRequest.getAccessToken());
        redisTemplate.opsForValue()
                .set(userLogoutRequest.getAccessToken(), "logout", expiredTime, TimeUnit.MILLISECONDS);

        return new UserLogoutResponse(String.format("%s 님이 logout에 성공했습니다", authentication.getName()));
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
    public UserProfileResponse updateUserInfoByUserName(String userName, UserProfileRequest userProfileRequest) {

        // 비밀번호 확인 로직 따로 빼야할 필요 있음
        if (!userProfileRequest.getPassword().equals(userProfileRequest.getPasswordConfirm())){
            throw new AppException(ErrorCode.NOT_MATCH_PASSWORD, ErrorCode.NOT_MATCH_PASSWORD.getMessage());
        }

        // user id 확인
        Optional<User> myUserOptional = userRepository.findByUserName(userName);

        if(myUserOptional.isEmpty()){
            throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        User beforeMyUser = myUserOptional.get();
        // request에서 수정된 정보만 반영하기

        String revisedUserName = (userProfileRequest.getUserName().equals(null))? beforeMyUser.getUsername(): userProfileRequest.getUserName();
        String revisedAddress = (userProfileRequest.getAddress().equals(null))? beforeMyUser.getAddress(): userProfileRequest.getAddress();
        String revisedPassword = (userProfileRequest.getPassword().equals(null))? beforeMyUser.getPassword(): userProfileRequest.getPassword();
        Boolean revisedLikeSoccer = (userProfileRequest.getLikeSoccer().equals(beforeMyUser.getSport().isSoccer()))? beforeMyUser.getSport().isSoccer(): userProfileRequest.getLikeSoccer();
        Boolean revisedLikeJogging = (userProfileRequest.getLikeJogging().equals(beforeMyUser.getSport().isJogging()))? beforeMyUser.getSport().isJogging(): userProfileRequest.getLikeJogging();
        Boolean revisedLikeTennis = (userProfileRequest.getLikeTennis().equals(beforeMyUser.getSport().isTennis()))? beforeMyUser.getSport().isTennis(): userProfileRequest.getLikeTennis();

        String encodedPassword = encrypterConfig.encoder().encode(revisedPassword);


        User revisedMyUser = User.toRevisedEntity(beforeMyUser.getId(), beforeMyUser.getUserId(), revisedUserName, revisedAddress, encodedPassword, revisedLikeSoccer, revisedLikeJogging, revisedLikeTennis);

        userRepository.save(revisedMyUser);

        return UserProfileResponse.fromEntity(revisedMyUser);

    }
    public UserProfileResponse getUserInfoByUserName(String userName) {

        Optional<User> selectedUserOptional = userRepository.findByUserName(userName);

        if(selectedUserOptional.isEmpty()){
            throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        User selectedUser = selectedUserOptional.get();

        return UserProfileResponse.fromEntity(selectedUser);

    }

    public String getProfilePathByUserName(String userName) {

        Optional<User> selectedUserOptional = userRepository.findByUserName(userName);

        if(selectedUserOptional.isEmpty()){
            throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        User selectedUser = selectedUserOptional.get();

        return selectedUser.getImagePath();

    }



}

