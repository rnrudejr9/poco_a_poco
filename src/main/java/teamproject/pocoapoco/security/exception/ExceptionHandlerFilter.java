package teamproject.pocoapoco.security.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import teamproject.pocoapoco.exception.ErrorCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

import static teamproject.pocoapoco.security.filter.JwtTokenFilter.MakeError;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    /**
     * 토큰 관련 에러 핸들링
     * JwtTokenFilter 에서 발생하는 에러를 핸들링
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {

            //토큰의 유효기간 만료
            log.error("만료된 토큰입니다");
            MakeError(response, ErrorCode.EXPIRED_TOKEN);

        } catch (JwtException | IllegalArgumentException e) {

            //유효하지 않은 토큰
            log.error("유효하지 않은 토큰이 입력되었습니다.");
            MakeError(response, ErrorCode.INVALID_TOKEN);

        } catch (NoSuchElementException e) {

            //사용자 찾을 수 없음
            log.error("사용자를 찾을 수 없습니다.");
            MakeError(response, ErrorCode.USERID_NOT_FOUND);

        } catch (ArrayIndexOutOfBoundsException e) {

            log.error("토큰을 추출할 수 없습니다.");
            MakeError(response, ErrorCode.INVALID_TOKEN);

        } catch (NullPointerException e) {

            filterChain.doFilter(request, response);
        }
    }

}
