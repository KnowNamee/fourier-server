package com.example.demo.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("download")
public class FileDownloaderController {

    @GetMapping(
            value = "{extension}/{fileName}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource getFile(
            @PathVariable("extension") String extension,
            @PathVariable("fileName") String fileName) {
        return new FileSystemResource(String.format("src/main/resources/%s/%s",
                extension, fileName));
    }

}