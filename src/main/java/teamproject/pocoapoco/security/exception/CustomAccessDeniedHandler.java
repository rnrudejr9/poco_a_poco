package teamproject.pocoapoco.security.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.security.filter.JwtTokenFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 4. 토큰 인증 후 권한 거부
        ErrorCode errorCode = ErrorCode.FORBIDDEN_REQUEST;
        JwtTokenFilter.MakeError(response, errorCode);
    }
}