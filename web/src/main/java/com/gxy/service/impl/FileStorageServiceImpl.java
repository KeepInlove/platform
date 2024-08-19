package com.gxy.service.impl;

import com.gxy.service.FileStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @Classname FileStorageServiceImpl
 * @Date 2024/8/16
 * @Created by guoxinyu
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String storageDir = "D:/uploads/";

    public String storeFile(MultipartFile file)  {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = Paths.get(storageDir + fileName);
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
        }catch (IOException e){
            throw new RuntimeException("Failed to store file: " + fileName, e);
        }

        return fileName;
    }

    public String getFilePath(String fileName) {
        return storageDir + fileName;
    }
}
