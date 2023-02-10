package teamproject.pocoapoco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamproject.pocoapoco.domain.dto.user.UserProfileResponse;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPhotoService {
    private final UserRepository userRepository;
    private final EncrypterConfig encrypterConfig;


    @Transactional
    public UserProfileResponse editUserImage(String userName, String imagePath) {

        Optional<User> selectedUserOptional = userRepository.findByUserName(userName);

        if(selectedUserOptional.isEmpty()){
            throw new AppException(ErrorCode.USERID_NOT_FOUND, ErrorCode.USERID_NOT_FOUND.getMessage());
        }

        User selectedUser = selectedUserOptional.get();

        User revisedUser = User.toEntityWithImage(selectedUser.getId(), selectedUser.getUsername(), selectedUser.getNickName(),
                selectedUser.getAddress(), selectedUser.getPassword(), selectedUser.getSport().isSoccer(),
                selectedUser.getSport().isJogging(), selectedUser.getSport().isTennis(), imagePath, selectedUser.getEmail());

        userRepository.save(revisedUser);


        return UserProfileResponse.fromEntity(revisedUser);

    }


}
