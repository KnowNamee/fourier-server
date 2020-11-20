package com.example.demo.controllers;

import com.example.demo.model.user.UserRegistrationRequest;
import com.example.demo.model.user.service.UserRegistrationService;
import com.example.demo.security.authorities.ApplicationUserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RegistrationController {

    UserRegistrationService userRegistrationService;

    @Autowired
    public RegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("registration/user")
    @PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
    public void registerNewUser(HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        UserRegistrationRequest userRegistrationRequest = getUserRegistrationRequestBody(request);
        userRegistrationService.register(userRegistrationRequest, ApplicationUserRole.USER);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private UserRegistrationRequest getUserRegistrationRequestBody(HttpServletRequest request) throws IOException {
        return new ObjectMapper().readValue(request.getInputStream(), UserRegistrationRequest.class);
    }

}
