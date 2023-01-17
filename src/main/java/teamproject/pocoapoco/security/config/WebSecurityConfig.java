package teamproject.pocoapoco.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import teamproject.pocoapoco.security.filter.JwtTokenFilter;
import teamproject.pocoapoco.security.provider.JwtProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtProvider jwtProvider;
    public static final String[] GET_AUTHENTICATED_REGEX_LIST = {
            "^/api/v1/test1$"
    };

    public static final String[] POST_AUTHENTICATED_REGEX_LIST = {
            "^/api/v1/test1$"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.cors();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //jwt 사용 간 설정

        http.authorizeHttpRequests()
                .regexMatchers(HttpMethod.GET, GET_AUTHENTICATED_REGEX_LIST).permitAll()
                .regexMatchers(HttpMethod.POST, POST_AUTHENTICATED_REGEX_LIST).authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        MakeError(response);
                    }
                }).accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        MakeError(response);
                    }
                });

//        http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedEntryPoint())
//                .and()
//                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        http.addFilterBefore(new JwtTokenFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public void MakeError(HttpServletResponse response /*, ErrorCode error*/) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

//        Response<ErrorResponse> resultResponse = ResultResponse.error(new InvalidPermissionException());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString("resultResponse"));
        //resultResponse 채워줘야됨
    }
}
