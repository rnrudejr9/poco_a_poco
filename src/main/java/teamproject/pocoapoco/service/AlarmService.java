package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.alarm.AlarmResponse;
import teamproject.pocoapoco.domain.entity.Alarm;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.AlarmRepository;
import teamproject.pocoapoco.repository.UserRepository;

import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public Page<AlarmResponse> getAlarms(Pageable pageable, String username) {
        User user = findByUserName(username);
        Page<Alarm> alarms = alarmRepository.findByUser(user, pageable);

        Page<AlarmResponse> alarmResponses = alarms.map(alarm -> AlarmResponse.fromEntity(alarm, userRepository.findByUserName(alarm.getFromUserName()).get().getImagePath()));
        return alarmResponses;
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(()-> new AppException(ErrorCode.USERID_NOT_FOUND,ErrorCode.USERID_NOT_FOUND.getMessage()));
    }
}
