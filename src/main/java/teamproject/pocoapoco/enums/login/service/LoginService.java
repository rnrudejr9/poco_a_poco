package teamproject.pocoapoco.enums.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.enums.login.logindto.UserLoginRequest;
import teamproject.pocoapoco.enums.login.jwt.JwtTokenUtil;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.login.logindto.Response;
import teamproject.pocoapoco.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;

    //시큐리티 적용 필요
//    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String key;

    private Long expireTimesMs = 1000 * 60 * 60L;


    public Response login(UserLoginRequest userLoginRequest) throws Exception {

        // userName 유효성 확인
        // 예외처리 수정 필요
        User user = userRepository.findByUserName(userLoginRequest.getUserName())
                .orElseThrow(() -> {throw new RuntimeException(String.valueOf(HttpStatus.NOT_FOUND));
                });

        // password 유효성 확인
        // 시큐리티 인코터 적용 후 재수정 필요
        if (!userLoginRequest.getPassword().equals(user.getPassword())){
            throw new RuntimeException(String.valueOf(HttpStatus.UNAUTHORIZED));
        }

        //token 발행
        return Response.success(JwtTokenUtil.creatToken(user.getUserName(), key, expireTimesMs));
    }


}
