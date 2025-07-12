package com.pm.whatsappclone.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j(topic = "FILE_SERVICE")
@RequiredArgsConstructor
public class FileService {

    @Value("${application.file.uploads.path}")
    private String fileUploadPath;


    public String saveFile(
            @NonNull MultipartFile sourceFile,
            @NonNull String userId) {
        final String fileUploadSubPath = "users" + separator + userId;
        

        return uploadFile(sourceFile, fileUploadPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile, String fileUploadSubPath) {

        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;

        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean created = targetFolder.mkdirs();
            if (!created) {
                log.error("Failed to create directory: {}", finalUploadPath);
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + separator + System.currentTimeMillis() + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try{
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved successfully at: {}", targetFilePath);
            return targetFilePath;
        }catch (IOException e){
            log.error("Failed to write file to path: {}", targetFilePath, e);
        }



        return null;

    }

    private String getFileExtension(String fileName) {
        if(fileName.isEmpty() || fileName == null){
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if(lastDotIndex == -1){
            return "";
        }

        return fileName.substring(lastDotIndex + 1).toLowerCase();

    }
}
