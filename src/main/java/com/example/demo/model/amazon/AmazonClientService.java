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

    public String uploadFile(File pdfFile) throws IOException {
        String url = "";
        String fileName = generateFilename(pdfFile);
        url = String.format("%s/%s/%s", clientConfig.getEndpointUrl(), clientConfig.getBucketName(), fileName);
        s3Client.putObject(new PutObjectRequest(clientConfig.getBucketName(), fileName, pdfFile));
        pdfFile.delete();
        return url;
    }

    private File multipartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

    private String generateFilename(File file) {
        return new Date().getTime() + "-" + Objects.requireNonNull(
                file.getName()).replace(" ", "_");
    }

}
