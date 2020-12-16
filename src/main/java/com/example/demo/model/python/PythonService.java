package com.example.demo.model.python;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class PythonService {

    private PythonConfig pythonConfig;

    @Autowired
    public PythonService(PythonConfig pythonConfig) {
        this.pythonConfig = pythonConfig;
    }

    public String convertToPdf(MultipartFile multipartFile) throws IOException {
        String filename = multipartToFile(multipartFile);
        String newFilename = filename.substring(0, filename.lastIndexOf('.')) + ".pdf";
        String tempFolder = pythonConfig.getTempFolder().substring(pythonConfig.getTempFolder().indexOf('/') + 1);
        String command = String.format("%s %s %s %s",
                pythonConfig.getInterpreter(),
                pythonConfig.getScript(),
                tempFolder + filename,
                tempFolder + newFilename);
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));
        String answer;
        String s;
        StringBuilder answerBuilder = new StringBuilder();
        while ((s = stdError.readLine()) != null) {
            answerBuilder.append(s);
        }
        answer = answerBuilder.toString();
//        return newFilename;
        return answer;
    }

    private String multipartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(pythonConfig.getTempFolder() + Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        file.createNewFile();
        return file.getName();
    }

}
