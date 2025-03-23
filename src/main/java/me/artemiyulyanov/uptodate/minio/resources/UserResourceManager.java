package me.artemiyulyanov.uptodate.minio.resources;

import lombok.*;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class UserResourceManager implements ResourceManager<User> {
    public static final String RESOURCES_FOLDER = "users/%d";

    @Autowired
    private MinioService minioService;

    @Override
    public List<String> uploadResources(User user, List<MultipartFile> files) {
        MultipartFile icon = files.get(0);

        if (icon != null) {
            String iconObjectKey = getResourceFolder(user) + File.separator + icon.getOriginalFilename();
            String url = minioService.uploadFile(iconObjectKey, icon);

            return List.of(url);
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> updateResources(User user, List<MultipartFile> files) {
        deleteResources(user);
        MultipartFile icon = files.get(0);

        if (icon != null) {
            String iconObjectKey = getResourceFolder(user) + File.separator + icon.getOriginalFilename();
            String url = minioService.uploadFile(iconObjectKey, icon);

            return List.of(url);
        }

        return Collections.emptyList();
    }

    @Override
    public void deleteResources(User user, List<String> filesNames) {
        filesNames.forEach(fileName -> minioService.deleteFile(getResourceFolder(user) + File.separator + fileName));
    }

    @Override
    public void deleteResources(User user) {
        if (minioService.folderExists(getResourceFolder(user))) minioService.deleteFolder(getResourceFolder(user));
    }

    @Override
    public String getResourceFolder(User user) {
        return String.format(RESOURCES_FOLDER, user.getId());
    }

    @Override
    public List<String> getResources(User user) {
        return minioService.getFolder(getResourceFolder(user));
    }
}
