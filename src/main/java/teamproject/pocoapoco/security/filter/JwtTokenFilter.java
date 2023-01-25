package teamproject.pocoapoco.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import teamproject.pocoapoco.domain.dto.error.ErrorResponse;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.security.provider.JwtProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    /**
     * request 에서 전달받은 Jwt 토큰을 확인
     */

    private final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        request.setAttribute("existsToken", true); // 토큰 존재 여부 초기화
        if (isEmptyToken(token)) request.setAttribute("existsToken", false); // 토큰이 없는 경우 false로 변경

        if (token == null || !token.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        token = parseBearer(token);

        if (jwtProvider.validateToken(token)) {
            // Redis 에 해당 accessToken logout 여부 확인
            String isLogout = (String)redisTemplate.opsForValue().get(token);
            log.info("isLogout?:{}",isLogout);

            if (ObjectUtils.isEmpty(isLogout)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else if(isLogout.equals("logout")) {
                MakeError(response,ErrorCode.INVALID_PERMISSION);
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isEmptyToken(String token) {
        return token == null || "".equals(token);
    }

    private String parseBearer(String token) {
        return token.substring(BEARER.length());
    }

    /**
     * Security Chain 에서 발생하는 에러 응답 구성
     */
    public static void MakeError(HttpServletResponse response , ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorResponse errorResponse = new ErrorResponse
                (errorCode, errorCode.getMessage());
        Response<ErrorResponse> resultResponse = Response.error(errorResponse);

        // 한글 출력을 위해 getWriter()
        response.getWriter().write(objectMapper.writeValueAsString(resultResponse));
    }
}
