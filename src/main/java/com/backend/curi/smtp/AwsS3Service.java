package com.backend.curi.smtp;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.backend.curi.smtp.dto.PreSignedUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.url}")
    private String cloudFrontPath;

    @Value("${cloud.aws.cloudfront.keypair-id}")
    private String keyPairId;

    private final AmazonS3 amazonS3Client;
    private final AmazonCloudFront amazonCloudFront;
//    public String upload(MultipartFile multipartFile, String dirName){
//        String fileName = createFileName(multipartFile.getOriginalFilename(), dirName);
//
//        try(InputStream inputStream = multipartFile.getInputStream()){
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setContentType(multipartFile.getContentType());
//            objectMetadata.setContentLength(multipartFile.getSize());
//            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
//        } catch (IOException e){
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
//        }
//
//        String url = amazonS3Client.getUrl(bucket, fileName).getPath();
//        return cloudFrontPath + url;
//    }

    private String createFileName(String fileName) {
        return UUID.randomUUID() + fileName;
    }

    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    private Date getSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(getPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());
        return generatePresignedUrlRequest;
    }

    public PreSignedUrl getUniqueObjectPreSignedUrl(String prefix, String fileName) {

        String onlyOneFileName = createFileName(fileName);

        if (!prefix.isEmpty()) {
            onlyOneFileName = prefix + "/" + onlyOneFileName;
        }
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, onlyOneFileName);

        return PreSignedUrl.builder()
                .fileName(onlyOneFileName)
                .preSignedUrl(amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString())
                .build();
    }

    public String getPreSignedUrl(String path) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, path);
        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    public String getSignedUrl(String path) {
        String signedURL;
        try {
            String resourcePath = cloudFrontPath + path;
            Date dateLessThan = getSignedUrlExpiration();

            String customPolicyForSignedUrl = CloudFrontUrlSigner.buildCustomPolicyForSignedUrl(
                    resourcePath, dateLessThan, null, null
            );

            File privateKeyFile = new ClassPathResource("key.pem").getFile();
            PrivateKey privateKey = SignerUtils.loadPrivateKey(privateKeyFile);

            signedURL = CloudFrontUrlSigner.getSignedURLWithCustomPolicy(
                    resourcePath,
                    keyPairId,
                    privateKey,
                    customPolicyForSignedUrl
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "url 생성에 실패했습니다.");
        }
        return signedURL;
    }
    public boolean isValidimageName(String fileName) {
        // 경로 구분 문자 또는 문제가 될 수 있는 특수 문자가 포함되어 있는지 확인
        Pattern pattern = Pattern.compile("[<>:\"/\\\\|?*]");
        if (pattern.matcher(fileName).find()) {
            return false;
        }

        // 파일 확장자를 얻기
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return false; // 확장자가 없음
        }

        String fileExtension = fileName.substring(lastIndex);

        // 확장자가 .jpg 또는 .png인지 확인
        return ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension);
    }

    public boolean isValidAttachmentName(String fileName, List<String> extensions) {
        if(extensions.isEmpty())
            return true;
        // 경로 구분 문자 또는 문제가 될 수 있는 특수 문자가 포함되어 있는지 확인
        Pattern pattern = Pattern.compile("[<>:\"/\\\\|?*]");
        if (pattern.matcher(fileName).find()) {
            return false;
        }

        // 파일 확장자를 얻기
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return false; // 확장자가 없음
        }

        String fileExtension = fileName.substring(lastIndex);

        for(var extension : extensions)
            if(extension.equalsIgnoreCase(fileExtension))
                return true;

        // 확장자가 .jpg 또는 .png인지 확인
        return ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension);
    }

    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(bucket, fileName);
    }
}