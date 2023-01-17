package teamproject.pocoapoco.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import teamproject.pocoapoco.security.exception.CustomAccessDeniedHandler;
import teamproject.pocoapoco.security.exception.CustomAuthenticationEntryPointHandler;
import teamproject.pocoapoco.security.exception.ExceptionHandlerFilter;
import teamproject.pocoapoco.security.filter.JwtTokenFilter;
import teamproject.pocoapoco.security.provider.JwtProvider;


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
                .authenticationEntryPoint(new CustomAuthenticationEntryPointHandler())
                .accessDeniedHandler(new CustomAccessDeniedHandler());

        http.addFilterBefore(new JwtTokenFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new ExceptionHandlerFilter(), JwtTokenFilter.class);

        return http.build();
    }
}
