package com.teamside.project.alpha.image_service.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.image_service.model.enumurate.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageServiceController {
    private final AmazonS3Client amazonS3Client;
    @Value("${aws.S3.bucket}")
    private String s3Bucket;

    @PostMapping("/upload")
    public ResponseEntity<ResponseObject> upload(MultipartFile image, @RequestParam(required = true) ImageType type) throws CustomException, IOException {
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(image.getContentType());
        objectMetaData.setContentLength(image.getSize());

        StringBuilder url = new StringBuilder().append("/dev");
        switch (type) {
            case REVIEW:
                url.append("/review/");
                break;
            case DAILY:
                url.append("/daily/");
                break;
            case GROUP:
                url.append("/group/");
                break;
            case PROFILE:
                url.append("/profile/");
                break;
            default:
                throw new CustomException(ApiExceptionCode.SYSTEM_ERROR);
        }
        url.append(UUID.randomUUID());


        //업로드
        amazonS3Client.putObject(
                new PutObjectRequest(s3Bucket, url.toString(), image.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );


        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(url.toString());
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}
