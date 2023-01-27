package teamproject.pocoapoco.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class ReIssueRequest {
    private String refreshToken;
    private String accessToken;
}
