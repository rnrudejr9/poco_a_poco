package teamproject.pocoapoco.config.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import teamproject.pocoapoco.config.oauth.provider.FacebookUserInfo;
import teamproject.pocoapoco.config.oauth.provider.GoogleUserInfo;
import teamproject.pocoapoco.config.oauth.provider.NaverUserInfo;
import teamproject.pocoapoco.config.oauth.provider.OAuth2UserInfo;
import teamproject.pocoapoco.domain.entity.Sport;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.enums.UserRole;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.provider.JwtProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;
    private final HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("getClientRegistration = {}", userRequest.getClientRegistration());
        log.info("getAccessToken = {}", userRequest.getAccessToken());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttribute = {}", super.loadUser(userRequest));

        String domain = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = null;
        // check 함수로 빼기
        if(domain.equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (domain.equals("facebook")) {
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if (domain.equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }else {
            System.out.println("구글과 페이스북과 네이버만 지원합니다.");
        }

        // 함수를 통해 구현해보기
        String provider = oAuth2UserInfo.getProvider(); // google
        String providerId = oAuth2UserInfo.getProviderId();
        String username = oAuth2UserInfo.getName();
        String userId = provider + "_" + providerId; //google_3894u2138413
        String password = encoder.encode("poco a poco");
        String email = oAuth2UserInfo.getEmail();

        User user = userRepository.findOauthUser(userId);
        if (user == null) {
            log.info("Oauth로그인이 최초입니다.");
            user = User.builder()
                    .userId(userId)
                    .userName(username)
                    .password(password)
                    .email(email)
                    .role(UserRole.ROLE_USER)
                    .provider(provider)
                    .providerId(providerId)
                    .sport(new Sport())
                    .build();
            userRepository.save(user);
        } else log.info("이미 Oauth 로그인을 한 적이있습니다.");

        String token = jwtProvider.generateAccessToken(user);
        session.setAttribute("Authorization", "Bearer " + token);
        session.setMaxInactiveInterval(60 * 25);
        return oAuth2User;
    }

}
