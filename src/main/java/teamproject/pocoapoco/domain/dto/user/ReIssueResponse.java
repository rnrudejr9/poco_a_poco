package teamproject.pocoapoco.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReIssueResponse {
    private String refreshToken;
    private String accessToken;
}
