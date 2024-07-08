package com.hrblizz.fileapi.data.entities;

import com.mongodb.DBObject;
import com.mongodb.lang.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDate;
import java.util.Date;

@Document(collection = "files")
public class UploadedFile {
    @Id
    private String id;

    @Field(name = "token")
    private String token;

    @Field(name = "filename")
    private String filename;

    @Field(name = "size")
    private String size;

    @Field(name = "contentType")
    private String contentType;

    @Field(name = "source")
    private String source;

    @Field(name = "meta")
    @Nullable
    private DBObject meta;

    @Nullable
    @Field(name = "expireTime")
    private LocalDate expireTime;

    @CreatedDate
    @Field(name = "createTime")
    private Date createTime;

    @Nullable public LocalDate getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(@Nullable LocalDate expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean isValid() {
        return this.token != null;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Nullable public DBObject getMeta() {
        return meta;
    }

    public void setMeta(@Nullable DBObject meta) {
        this.meta = meta;
    }

}
