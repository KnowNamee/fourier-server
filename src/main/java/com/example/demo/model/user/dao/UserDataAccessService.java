package com.example.demo.model.user.dao;

import com.example.demo.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDataAccessService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDataAccessService(JdbcTemplate jdbcTemplate,
                                 PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void insertUser(User user) throws DataAccessException {
        String sql = getUserCredentialsInsertionSql(user).concat(getUserAuthoritiesInsertionSql(user));
        jdbcTemplate.update(sql);
    }

    public void insertUserFile(String username, String filename) {
        String sql = getInsertUserFileSql(username, filename);
        jdbcTemplate.update(sql);
    }

    public void deleteUserFile(String username, String filename) {
        String sql = getDeleteUserFileSql(username, filename);
        jdbcTemplate.update(sql);
    }

    private String getDeleteUserFileSql(String username, String filename) {
        return String.format(
                "delete from user_files where username = '%s' and filename = '%s';",
                username,
                filename);
    }

    private String getInsertUserFileSql(String username, String filename) {
        return String.format(
                "insert into user_files (username, filename) values ('%s', '%s');",
                username,
                filename);
    }

    private String getUserCredentialsInsertionSql(User user) {
        return String.format(
                "insert into users (email, username, password) values  ('%s', '%s', '%s');",
                user.getEmail(),
                user.getUsername(),
                passwordEncoder.encode(user.getPassword()));
    }

    private String getUserAuthoritiesInsertionSql(User user) {
        StringBuilder sql = new StringBuilder();
        for (GrantedAuthority authority : user.getAuthorities()) {
            sql.append(String.format(
                    "insert into user_authorities (username, authority) values ('%s', '%s');\n",
                    user.getUsername(),
                    authority.getAuthority()));
        }
        return sql.toString();
    }

}
