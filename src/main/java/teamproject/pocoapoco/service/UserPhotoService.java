package teamproject.pocoapoco.service;

import io.github.classgraph.Resource;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import teamproject.pocoapoco.domain.dto.user.UserProfileResponse;
import teamproject.pocoapoco.domain.entity.User;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;
import teamproject.pocoapoco.repository.UserRepository;
import teamproject.pocoapoco.security.config.EncrypterConfig;

import javax.transaction.Transactional;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        User revisedUser = User.toEntityWithImage(selectedUser.getId(), selectedUser.getUserId(), selectedUser.getUsername(),
                selectedUser.getAddress(), selectedUser.getPassword(), selectedUser.getSport().isSoccer(),
                selectedUser.getSport().isJogging(), selectedUser.getSport().isTennis(), imagePath);

        userRepository.save(revisedUser);


        return UserProfileResponse.fromEntity(revisedUser);

    }


}
