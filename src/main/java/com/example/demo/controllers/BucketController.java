package com.example.demo.controllers;

import com.example.demo.model.amazon.AmazonClientService;
import com.example.demo.model.user.service.UserFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/bucket/")
public class BucketController {

    private AmazonClientService amazonClientService;
    private UserFilesService userFilesService;

    @Autowired
    public BucketController(AmazonClientService amazonClientService, UserFilesService userFilesService) {
        this.amazonClientService = amazonClientService;
        this.userFilesService = userFilesService;
    }

    @PutMapping("/upload")
    @PreAuthorize("hasAuthority('file:upload')")
    public ResponseEntity<?> upload(@RequestPart(value = "file") MultipartFile multipartFile, Principal principal) {
        // TODO Add python file processing here
        String url = "";
        try {
            url = amazonClientService.uploadFile(multipartFile);
            userFilesService.addFile(principal.getName(), url.substring(url.lastIndexOf("/") + 1));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(url);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('file:delete')")
    public ResponseEntity<?> delete(@RequestPart("filename") String filename, Principal principal) {
        try {
            amazonClientService.deleteFile(filename);
            userFilesService.deleteFile(principal.getName(), filename);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("File deleted successfully");
    }

}
