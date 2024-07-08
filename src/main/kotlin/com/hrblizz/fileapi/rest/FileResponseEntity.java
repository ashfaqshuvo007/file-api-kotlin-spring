package com.hrblizz.fileapi.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import java.util.List;

public class FileResponseEntity<T> extends ResponseEntity<T> {
    public FileResponseEntity(T body, MultiValueMap<String, String> headers, int rawStatus) {
        super(body, headers, rawStatus);
    }
}
