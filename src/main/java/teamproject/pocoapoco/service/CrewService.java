package teamproject.pocoapoco.service;


import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.crew.CrewAddRequest;
import teamproject.pocoapoco.domain.dto.crew.CrewAddResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
// DTO => Entity
// Entity => DTO
@RequiredArgsConstructor
@ToString
@Slf4j
public class CrewService {

    private final CrewRepository crewRepository;
    private final UserRepository userRepository;


    // Crew 게시글 등록
    public CrewAddResponse addCrew(CrewAddRequest request, String userName) {

        // userName 존재 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> {
                    throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
                });

        // DB 저장
        Crew crew = request.toEntity(user);
        crewRepository.save(crew);

        return new CrewAddResponse("Crew 등록 완료", crew.getId());
    }


}
