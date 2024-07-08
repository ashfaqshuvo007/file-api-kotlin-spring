package com.hrblizz.fileapi.controller;

import com.hrblizz.fileapi.controller.exception.BadRequestException;
import com.hrblizz.fileapi.controller.exception.NotFoundException;
import com.hrblizz.fileapi.data.entities.UploadedFile;
import com.hrblizz.fileapi.library.RestExceptionHandler;
import com.hrblizz.fileapi.library.log.LogItem;
import com.hrblizz.fileapi.library.log.Logger;
import com.hrblizz.fileapi.rest.ErrorMessage;
import com.hrblizz.fileapi.rest.FileResponseEntity;
import com.hrblizz.fileapi.rest.ResponseEntity;
import com.hrblizz.fileapi.service.FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileApiController {

    @Autowired
    private FileService fileService;

    @Autowired
    private RestExceptionHandler restExceptionHandler;


    private Logger LOG;

    @RequestMapping(value = "/files", method = { RequestMethod.POST})
    public ResponseEntity<?> uploadFile(
        @RequestParam("name") String name,
        @RequestParam("contentType") String contentType,
        @RequestParam("source") String source,
        @RequestParam("expireTime") String expireTime,
        @RequestParam("content") MultipartFile content){

        List<ErrorMessage> errorMessageList = new ArrayList<>();
        int status = HttpStatus.CREATED.value();
        Map<String, Object> uploadDetails =
            fileService.addFile(name, contentType, source, expireTime, content);

        if (uploadDetails.containsKey("exception")) {
            errorMessageList.add(
                new ErrorMessage(uploadDetails.get("exception").toString()));
            status = HttpStatus.SERVICE_UNAVAILABLE.value();
        }

        return new ResponseEntity<>(uploadDetails, errorMessageList, status);
    }

    @RequestMapping(value = "/file/{token}", method = { RequestMethod.GET})
    public FileResponseEntity<?> getFileByToken(@PathVariable String token) {

        List<ErrorMessage> errorMessageList = new ArrayList<>();

        if (Objects.equals(token, "")) {

            LOG.info(new LogItem("Bad request: No token param in URL"));
            errorMessageList.add(
                new ErrorMessage(
                    "Bad request: No token param in URL", "400"));
            throw new BadRequestException("Empty token in url!");

        }

        UploadedFile fileMetaData = fileService.findByToken(token);

        if (!(fileMetaData == null)) {

            Resource fileResource = fileService.downloadFile(
                fileMetaData.getFilename());

            if (fileResource == null) {
                LOG.info(new LogItem("File Not Found!"));
                errorMessageList.add(
                    new ErrorMessage("File not found!", "404"));
                throw new NotFoundException("File not found!");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Filename", fileMetaData.getFilename());
            headers.add("X-Filesize", fileMetaData.getSize());
            headers.add("X-CreateTime",
                fileMetaData.getCreateTime().toString());
            headers.add("Content-Type",
                String.valueOf(MediaType.parseMediaType(
                    fileMetaData.getContentType())));
            headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                    + fileMetaData.getFilename() + "\"");

            return new FileResponseEntity<>(
                fileResource,
                headers,
                HttpStatus.OK.value());

        } else {
            return new FileResponseEntity<>(
                errorMessageList,
                null,
                HttpStatus.NOT_FOUND.value());
        }

    }

    @RequestMapping(value = "/file/{token}",
        method = { RequestMethod.DELETE})
    public ResponseEntity<?> deleteByToken(@PathVariable String token) {

        List<ErrorMessage> errorMessageList = new ArrayList<>();
        String success = "File deleted Successfully";
        int status = HttpStatus.OK.value();

        Integer deleteId = fileService.deleteByToken(token);
        if (deleteId == 0) {
            errorMessageList.add(
                new ErrorMessage("File Not Found", "404"));
            success = "File Not Found";
            status = HttpStatus.NOT_FOUND.value();
        }

        return new ResponseEntity<>(
            success,
            errorMessageList,
            status
        );
    }

    @RequestMapping(value = "/files/metas", method = { RequestMethod.POST})
    public ResponseEntity<?> getFileMetas(
        @RequestBody Map<String,
        List<String>> payload) {

        List<ErrorMessage> errorMessageList = new ArrayList<>();

        if (payload.values().isEmpty()) {
            errorMessageList.add(
                new ErrorMessage(
                    "Empty Tokens list! Invalid Payload", "400 ")
            );
            throw new BadRequestException("Invalid Payload. Empty Json input");
        }

        if(payload.get("tokens").isEmpty()) {
            errorMessageList.add(
                new ErrorMessage(
                    "Empty Tokens list! Invalid Payload", "400 ")
            );
            throw new BadRequestException("Empty Tokens list! Invalid Payload");
        }

        Map<String, Map<String, UploadedFile>> queryResult =
            fileService.getFilesByTokens(payload);

        if (queryResult.get("files").containsValue(null)) {
            errorMessageList.add(
                new ErrorMessage(
                    "No file meta data found", "404"));
            throw new NotFoundException("No file meta data found");
        }

        return new ResponseEntity<>(
            queryResult,
            errorMessageList,
            HttpStatus.OK.value());
    }

}
