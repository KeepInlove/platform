package com.gxy.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname FileStorageService
 * @Date 2024/8/16
 * @Created by guoxinyu
 */
public interface FileStorageService {

    public String storeFile(MultipartFile file);

    public String getFilePath(String fileName);
}
