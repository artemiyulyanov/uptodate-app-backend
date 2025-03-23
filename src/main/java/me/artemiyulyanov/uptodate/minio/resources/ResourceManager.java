package me.artemiyulyanov.uptodate.minio.resources;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceManager<T> {
    List<String> uploadResources(T entity, List<MultipartFile> files);
    @Deprecated
    List<String> updateResources(T entity, List<MultipartFile> files);
    void deleteResources(T entity, List<String> filesNames);
    void deleteResources(T entity);

    String getResourceFolder(T entity);
    List<String> getResources(T entity);
}