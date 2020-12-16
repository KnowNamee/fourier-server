package com.example.demo.model.user.validator;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class PasswordValidator implements Predicate<String> {

    /** Method {@code isPasswordStructureValid} validates {@code password} structure
     * using regular expression.
     *
     * Regular expression contains :
     * <p>(?=.*[0-9])  - password must contain at least 1 digit</p>
     * <p>(?=.*[_])    - password must contain at least 1 symbol '_'</p>
     * <p><(?=.*[a-z]) - password must contain at least 1 symbol of [a-z]</p>
     * <p>(?=.*[A-Z])  - password must contain at least 1 symbol of [A-Z]</p>
     * <p>[0-9a-zA-Z_] - password contains only symbols [0-9a-zA-Z_]</p>
     * <p>      {6,30} - password length from 6 to 30</p>
     *
     * @param password password to validate
     * @return {@code true} if the input argument is valid, otherwise {@code false}
     */
    private static boolean isPasswordStructureValid(String password) {
        return Pattern.compile(
                "(?=.*[0-9])(?=.*[_])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z_]{6,30}"
        ).asPredicate().test(password);
    }

    @Override
    public boolean test(String password) {
        return isPasswordStructureValid(password);
    }

}
