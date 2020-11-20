package com.example.demo.model.user.service;

import com.example.demo.model.user.validator.EmailValidator;
import com.example.demo.model.user.validator.PasswordValidator;
import com.example.demo.model.user.UserRegistrationRequest;
import com.example.demo.model.user.validator.UsernameValidator;
import com.example.demo.model.user.User;
import com.example.demo.model.user.dao.UserDataAccessService;
import com.example.demo.security.authorities.ApplicationUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserDataAccessService userDataAccessService;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final UsernameValidator usernameValidator;

    @Autowired
    public UserRegistrationService(UserDataAccessService userDataAccessService,
                                   EmailValidator emailValidator,
                                   PasswordValidator passwordValidator,
                                   UsernameValidator usernameValidator) {
        this.userDataAccessService = userDataAccessService;
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.usernameValidator = usernameValidator;
    }

    public void register(UserRegistrationRequest userRegistrationRequest,
                         ApplicationUserRole role) throws DataAccessException {
        boolean isEmailValid = emailValidator.test(userRegistrationRequest.getEmail());
        boolean isUsernameValid = usernameValidator.test(userRegistrationRequest.getUsername());
        boolean isPasswordValid = passwordValidator.test(userRegistrationRequest.getPassword());

        if (isEmailValid && isUsernameValid && isPasswordValid) {
            userDataAccessService.insertUser(
                    new User(
                            userRegistrationRequest.getEmail(),
                            userRegistrationRequest.getUsername(),
                            userRegistrationRequest.getPassword(),
                            role.getGrantedAuthorities()
                    ));
        }
    }

}
