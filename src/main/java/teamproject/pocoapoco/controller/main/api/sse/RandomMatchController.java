package teamproject.pocoapoco.controller.main.api.sse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/match")
public class RandomMatchController {

    static int randomMatchCnt;

    @PostMapping("/random")
    public int randomMatch() {
        if(randomMatchCnt % 3 == 0) randomMatchCnt = 0;
        return ++randomMatchCnt;
    }

}
