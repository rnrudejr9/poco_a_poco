package teamproject.pocoapoco.service.manage;


import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.manage.CrewManageResponse;
import teamproject.pocoapoco.domain.dto.manage.UserManageResponse;
import teamproject.pocoapoco.domain.entity.Crew;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserRepository userRepository;
    private final CrewRepository crewRepository;

    public Page<UserManageResponse> getUsersInfo(){
        List<User> userList = userRepository.findAll();

        List<UserManageResponse> userManageResponses = userList.stream()
                .map(user -> UserManageResponse.fromEntity(user))
                .collect(Collectors.toList());

        Page<UserManageResponse> userManageResponsePage = new PageImpl<>(userManageResponses);


        return userManageResponsePage;
    }


    @Transactional
    public Page<CrewManageResponse> getCrewInfo(String strict, Pageable pageable){

        List<Crew> crewList;

        if(strict == null){
            crewList = crewRepository.findByDeletedAt(null, pageable);
        } else{
            crewList = crewRepository.findByDeletedAtAndStrictContaining(null, strict, pageable);
        }


        List<CrewManageResponse> crewManageResponses = crewList.stream()
                .map(crew -> CrewManageResponse.fromEntity(crew))
                .collect(Collectors.toList());

        Page<CrewManageResponse> crewManageResponsePage = new PageImpl<>(crewManageResponses);


        return crewManageResponsePage;
    }






}
