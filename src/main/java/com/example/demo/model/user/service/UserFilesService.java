package com.example.demo.model.user.service;

import com.example.demo.model.user.dao.UserDataAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFilesService {

    private UserDataAccessService userDataAccessService;

    @Autowired
    public UserFilesService(UserDataAccessService userDataAccessService) {
        this.userDataAccessService = userDataAccessService;
    }

    public void addFile(String username, String filename) {
        // TODO add exception if file can't be added
        // TODO check user plan / file limit
        userDataAccessService.insertUserFile(username, filename);
    }

    public void deleteFile(String username, String filename) {
        userDataAccessService.deleteUserFile(username, filename);
    }

}
