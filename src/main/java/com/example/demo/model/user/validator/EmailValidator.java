package com.example.demo.model.user.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class EmailValidator implements Predicate<String> {

    private static boolean isEmailStructureValid(String email) {
        return Pattern.compile(
                "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE
            ).asPredicate().test(email);
    }

    private static boolean isEmailLengthValid(String email) {
        return email.length() <= 255;
    }

    @Override
    public boolean test(String email) {
        return isEmailStructureValid(email) && isEmailLengthValid(email);
    }

}