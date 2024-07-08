package com.hrblizz.fileapi.service;

import com.hrblizz.fileapi.controller.exception.FileStorageException;
import com.hrblizz.fileapi.controller.exception.NotFoundException;
import com.hrblizz.fileapi.data.entities.UploadedFile;
import com.hrblizz.fileapi.data.repository.UploadedFileRepository;
import com.hrblizz.fileapi.library.FileStorageProperties;
import com.hrblizz.fileapi.library.log.ExceptionLogItem;
import com.hrblizz.fileapi.library.log.LogItem;
import com.hrblizz.fileapi.library.log.Logger;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService {

    public Logger LOG;
    private final Path fileStorageLocation;
    private final UploadedFileRepository fileRepository;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties,
                       UploadedFileRepository fileRepository) {

       this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
            .toAbsolutePath().normalize();
        this.fileRepository = fileRepository;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            LOG.error(new ExceptionLogItem("File Storage Exception", ex));
            throw new FileStorageException(
                "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public UploadedFile findByToken(String token){
        return fileRepository.findByToken(token);
    }

    public Resource downloadFile(String fileName) {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        try {
            return new FileSystemResource(filePath.toFile());
        } catch (NotFoundException e) {
            LOG.error(new ExceptionLogItem("File Not Found", e));
            return null;
        }
    }

    public Map<String, Object> addFile(
        String name,
        String contentType,
        String source,
        String expireTime,
        MultipartFile content) {

        // Normalize file name
        String fileName = StringUtils.cleanPath(
            Objects.requireNonNull(name));

        String file_token = UUID.randomUUID().toString();
        Map<String, Object> resDetails = new HashMap<>();
        resDetails.put("token", file_token);

        DBObject meta = new BasicDBObject();
        meta.put("createdEmployeeId", 1);
        LocalDate expireAt = LocalDate.parse(expireTime);

        try {
            // Check for filename validity
            if (fileName.contains("..")) {
                LOG.error(new LogItem("Invalid file name format"));
                throw new FileStorageException(
                    "Sorry! Filename contains invalid path sequence "
                    + fileName
                );
            }

            // Copy file to the target location(Replace file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(content.getInputStream(),
                targetLocation, StandardCopyOption.REPLACE_EXISTING);

            //Parsing Submitted Form Meta data
            UploadedFile fileDetails = new UploadedFile();
            fileDetails.setToken(file_token);
            fileDetails.setFilename(fileName);
            fileDetails.setSize(String.valueOf(content.getSize()));
            fileDetails.setMeta(meta);
            fileDetails.setSource(source);
            fileDetails.setContentType(contentType);
            fileDetails.setCreateTime(new Date());
            fileDetails.setExpireTime(expireAt);
            fileRepository.save(fileDetails);

        } catch (Exception ex) {
            resDetails.put("exception", ex);
        }

        return resDetails;
    }

    public Integer deleteByToken(String token){

        UploadedFile fileResource = this.findByToken(token);
        int deleteCount = 0;

        if(!(fileResource == null)){
            Path filePath =
                this.fileStorageLocation.resolve(fileResource.getFilename()).normalize();
            File file = new File(filePath.toString());
            boolean deleted = file.delete();

            if (deleted) {
                deleteCount = fileRepository.deleteByToken(token);
                return deleteCount;
            }

        } else {
            LOG.error(new LogItem(
                String.format("No File found on disk for token: %s", token )));
            throw new NotFoundException("File Not found");
        }

        return deleteCount;
    }

    public Map<String, Map<String, UploadedFile>>
        getFilesByTokens(Map<String,List<String>> payload) {

            List<String> tokens = payload.get("tokens");
            Map<String, UploadedFile> results = new HashMap<>();

            for (String token: tokens) {
                if (fileRepository.findByToken(token) == null) {
                    results.put(token, null);
                    LOG.error(new LogItem(
                        String.format("No metadata found for token: %s", token )));
                    throw new NotFoundException("No metadata found for token: " + token);
                }
                results.put(token, fileRepository.findByToken(token));
            }

            Map<String, Map<String, UploadedFile>> formattedResult
                = new HashMap<>();
            formattedResult.put("files", results);

            return formattedResult;
    }
}
