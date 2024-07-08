package com.hrblizz.fileapi.data.repository;

import com.hrblizz.fileapi.data.entities.UploadedFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Map;

public interface UploadedFileRepository extends MongoRepository<UploadedFile, String> {

    UploadedFile findByToken(String token);

    Integer deleteByToken(String token);

}
