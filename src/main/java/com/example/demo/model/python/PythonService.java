package com.example.demo.model.python;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Service
public class PythonService {

    public PythonService() { }

    public File convertToPdf (MultipartFile multipartFile) throws IOException {
        Process p = Runtime.getRuntime().exec(
                "C:\\Users\\Администратор\\AppData\\Local\\Programs\\Python\\Python37\\python.exe PyTest.py");
        return multipartToFile(multipartFile);
    }

    private File multipartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

}
