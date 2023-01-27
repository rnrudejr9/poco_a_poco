package teamproject.pocoapoco.service;

import io.github.classgraph.Resource;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import teamproject.pocoapoco.exception.AppException;
import teamproject.pocoapoco.exception.ErrorCode;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class UserPhotoService {
//
//    private Path rootLocation = Paths.get("upload-dir");
//
//    public void store(MultipartFile file) {
//        String filename = StringUtils.cleanPath(file.getOriginalFilename());
//        try {
//            if (file.isEmpty()) {
//                throw new AppException(ErrorCode.FILE_NOT_FOUND, ErrorCode.FILE_NOT_FOUND.getMessage());
//            }
//            if (filename.contains("..")) {
//                // This is a security check
//                throw new AppException(ErrorCode.);
//            }
//            try (InputStream inputStream = file.getInputStream()) {
//                Files.copy(inputStream, this.rootLocation.resolve(filename),
//                        StandardCopyOption.REPLACE_EXISTING);
//
//                ExecutorService executorService = Executors.newSingleThreadExecutor();
//                Future<String> future = executorService.submit(() -> {
//                    makeThumbnail(filename);
//                    return "created";
//                });
//            }
//        } catch (IOException e) {
//            throw new StorageException("Failed to store file " + filename, e);
//        }
//    }
//
//    private void makeThumbnail(String filename) throws IOException {
//        // Thumbnail create
//        String originPath = this.rootLocation.resolve(filename).toString();
//        String outputPath = this.rootLocation.resolve("th_" + filename).toString();
//        Thumbnails.of(originPath).size(80, 80).toFile(outputPath);
//    }
//
//    @Override
//    public Stream<Path> loadAll() {
//        try {
//            return Files.walk(this.rootLocation, 1)
//                    .filter(path -> !path.equals(this.rootLocation))
//                    .map(this.rootLocation::relativize);
//        } catch (IOException e) {
//            throw new StorageException("Failed to read stored files", e);
//        }
//
//    }
//
//    public Path load(String filename) {
//        return rootLocation.resolve(filename);
//    }
//
//
//    public Resource loadAsResource(String filename) throws MalformedURLException {
//        try {
//            Path file = load(filename);
//            Resource resource = new UrlResource(file.toUri());
//            if (resource.exists() || resource.isReadable()) {
//                return resource;
//            } else {
//                throw new StorageFileNotFoundException(
//                        "Could not read file: " + filename);
//
//            }
//        } catch (MalformedURLException e) {
//            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
//        }
//    }
//
//    @Override
//    public void deleteAll() {
//        FileSystemUtils.deleteRecursively(rootLocation.toFile());
//    }
//
//    @Override
//    public void init() {
//        try {
//            Files.createDirectories(rootLocation);
//        } catch (IOException e) {
//            throw new StorageException("Could not initialize storage", e);
//        }
//    }
}
