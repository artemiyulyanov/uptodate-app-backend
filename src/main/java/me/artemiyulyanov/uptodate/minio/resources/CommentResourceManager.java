package me.artemiyulyanov.uptodate.minio.resources;

import lombok.*;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.models.Comment;
import me.artemiyulyanov.uptodate.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class CommentResourceManager implements ResourceManager<Comment> {
    public static final String RESOURCES_FOLDER = "articles/%d/comments/%d";

    @Autowired
    private MinioService minioService;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<String> uploadResources(Comment comment, List<MultipartFile> files) {
        if (files != null) {
            List<String> resourcesUrls = files.stream()
                    .map(file -> minioService.uploadFile(getResourceFolder(comment) + File.separator + file.getOriginalFilename(), file))
                    .collect(Collectors.toCollection(ArrayList::new));

            return resourcesUrls;
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> updateResources(Comment comment, List<MultipartFile> files) {
        deleteResources(comment);

        if (files != null) {
            List<String> resourcesUrls = files.stream()
                    .map(file -> minioService.uploadFile(getResourceFolder(comment) + File.separator + file.getOriginalFilename(), file))
                    .collect(Collectors.toCollection(ArrayList::new));

            return resourcesUrls;
        }

        return Collections.emptyList();
    }

    @Override
    public void deleteResources(Comment comment, List<String> filesNames) {
        filesNames.forEach(fileName -> minioService.deleteFile(getResourceFolder(comment) + File.separator + fileName));
    }

    @Override
    public void deleteResources(Comment comment) {
        if (minioService.folderExists(getResourceFolder(comment))) minioService.deleteFolder(getResourceFolder(comment));
    }

    @Override
    public String getResourceFolder(Comment comment) {
        return String.format(RESOURCES_FOLDER, comment.getArticle().getId(), comment.getId());
    }

    @Override
    public List<String> getResources(Comment comment) {
        return minioService.getFolder(getResourceFolder(comment));
    }
}
