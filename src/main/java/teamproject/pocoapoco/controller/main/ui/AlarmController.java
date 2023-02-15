package teamproject.pocoapoco.controller.main.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import teamproject.pocoapoco.domain.dto.alarm.AlarmResponse;
import teamproject.pocoapoco.service.AlarmService;

import javax.transaction.Transactional;

@Controller
@RequestMapping("/view/v1")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmController {


    private final AlarmService alarmService;


    @Value("${aws.access.key}")
    String AWS_ACCESS_KEY;

    @Value("${aws.secret.access.key}")
    String AWS_SECRET_ACCESS_KEY;

    @Value("${aws.region}")
    String AWS_REGION;

    @Value("${aws.bucket.name}")
    String AWS_BUCKET_NAME;

    String AWS_BUCKET_DIRECTORY = "/profileimages";


    @GetMapping("/alarm")
    public String alarm(Model model, @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
            @SortDefault(sort = "lastModifiedAt", direction = Sort.Direction.DESC),
            @SortDefault(sort = "readOrNot", direction = Sort.Direction.DESC)
    }) Pageable pageable, Authentication authentication){

        model.addAttribute("AWS_ACCESS_KEY", AWS_ACCESS_KEY);
        model.addAttribute("AWS_SECRET_ACCESS_KEY", AWS_SECRET_ACCESS_KEY);
        model.addAttribute("AWS_REGION", AWS_REGION);
        model.addAttribute("AWS_BUCKET_NAME", AWS_BUCKET_NAME);
        model.addAttribute("AWS_BUCKET_DIRECTORY", AWS_BUCKET_DIRECTORY);


        Page<AlarmResponse> alarms = alarmService.getAlarms(pageable, authentication.getName());
        int startPage = Math.max(1,alarms.getPageable().getPageNumber() - 4);
        int endPage = Math.min(alarms.getTotalPages(),alarms.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("alarms", alarms);
        return "alarms/alarm";
    }
}
