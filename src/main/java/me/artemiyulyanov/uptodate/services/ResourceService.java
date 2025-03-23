package me.artemiyulyanov.uptodate.services;

import me.artemiyulyanov.uptodate.minio.resources.ResourceManager;

public interface ResourceService<T extends ResourceManager> {
    T getResourceManager();
}