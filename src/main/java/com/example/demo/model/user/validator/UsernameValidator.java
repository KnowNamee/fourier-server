package com.example.demo.model.user.validator;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class UsernameValidator implements Predicate<String> {

    private static boolean isUsernameStructureValid(String username) {
        return Pattern.compile(
                "[0-9a-zA-Z_]{3,20}"
        ).asPredicate().test(username);
    }

    @Override
    public boolean test(String username) {
        return isUsernameStructureValid(username);
    }

}
