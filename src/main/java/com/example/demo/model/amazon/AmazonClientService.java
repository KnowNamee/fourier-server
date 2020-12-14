package com.example.demo.model.amazon;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Service
public class AmazonClientService {

    private AmazonS3 s3Client;
    private AmazonClientConfig clientConfig;

    @Autowired
    public AmazonClientService(AmazonClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @PostConstruct
    private void InitializeS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(clientConfig.getAccessKey(),
                                                             clientConfig.getSecretKey());
        s3Client = new AmazonS3Client(credentials);
    }

    public void deleteFile(String filename) {
        s3Client.deleteObject(new DeleteObjectRequest(clientConfig.getBucketName(), filename));
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String url = "";
        File file = multipartToFile(multipartFile);
        String filename = generateFilename(multipartFile);
        url = String.format("%s/%s/%s", clientConfig.getEndpointUrl(), clientConfig.getBucketName(), filename);
        s3Client.putObject(new PutObjectRequest(clientConfig.getBucketName(), filename, file));
        file.delete();
        return url;
    }

    private File multipartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

    private String generateFilename(MultipartFile multipartFile) {
        return new Date().getTime() + "-" + Objects.requireNonNull(
                multipartFile.getOriginalFilename()).replace(" ", "_");
    }

}
