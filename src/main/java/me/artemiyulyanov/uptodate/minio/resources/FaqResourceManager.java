package me.artemiyulyanov.uptodate.minio.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.ContentBlock;
import me.artemiyulyanov.uptodate.models.FaqItem;
import me.artemiyulyanov.uptodate.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class FaqResourceManager implements ResourceManager<FaqItem> {
    public static final String RESOURCES_FOLDER = "faq_items/%d";

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<String> uploadResources(FaqItem item, List<MultipartFile> files) {
        if (files != null) {
            return files.stream()
                    .map(file -> minioService.uploadFile(getResourceFolder(item) + File.separator + file.getOriginalFilename(), file))
                    .toList();
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> updateResources(FaqItem item, List<MultipartFile> files) {
        deleteResources(item);

        if (files != null) {
            return files.stream()
                    .map(file -> minioService.uploadFile(getResourceFolder(item) + File.separator + file.getOriginalFilename(), file))
                    .toList();
        }

        return Collections.emptyList();
    }

    @Override
    public void deleteResources(FaqItem item, List<String> filesNames) {
        filesNames.forEach(fileName -> minioService.deleteFile(getResourceFolder(item) + File.separator + fileName));
    }

    @Override
    public void deleteResources(FaqItem item) {
        if (minioService.folderExists(getResourceFolder(item))) minioService.deleteFolder(getResourceFolder(item));
    }

    public List<ContentBlock> uploadContent(FaqItem item, List<ContentBlock> contentBlocks, List<MultipartFile> resources) {
        AtomicInteger index = new AtomicInteger(0);

        List<String> resourcesUrls = uploadResources(item, resources);

        return contentBlocks.stream()
                .map(contentBlock -> {
                    if (contentBlock.getType() == ContentBlock.ContentBlockType.IMAGE && contentBlock.getText().equals("expected-file")) {
                        contentBlock.setText(resourcesUrls.get(index.getAndIncrement()));
                        return contentBlock;
                    } else {
                        return contentBlock;
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String getResourceFolder(FaqItem item) {
        return String.format(RESOURCES_FOLDER, item.getId());
    }

    @Override
    public List<String> getResources(FaqItem item) {
        return minioService.getFolder(getResourceFolder(item));
    }
}