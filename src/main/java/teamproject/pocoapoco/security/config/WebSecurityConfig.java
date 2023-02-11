package teamproject.pocoapoco.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import teamproject.pocoapoco.config.oauth.PrincipalOauth2UserService;
import teamproject.pocoapoco.security.exception.CustomAccessDeniedHandler;
import teamproject.pocoapoco.security.exception.CustomAuthenticationEntryPointHandler;
import teamproject.pocoapoco.security.filter.JwtTokenFilter;
import teamproject.pocoapoco.security.provider.JwtProvider;


@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;

    private final PrincipalOauth2UserService principalOauth2UserService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/v1/users/join", "/api/v1/users/login", "/api/v1/users/regenerateToken","/api/v1/findPass","/api/v1/resetPass").permitAll()
                .antMatchers("/api/v1/resetPass/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/resetPass").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/users/regenerateToken").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                .antMatchers(HttpMethod.GET, "/view/v1/dashboard").permitAll()
                .and()
                .oauth2Login()
                .loginPage("/view/v1/start")
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
                .and()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPointHandler())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtTokenFilter(jwtProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .build();

    }

}