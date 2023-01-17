package teamproject.pocoapoco.domain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import teamproject.pocoapoco.exception.ErrorCode;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final ErrorCode errorCode;
    private final String message;
}
