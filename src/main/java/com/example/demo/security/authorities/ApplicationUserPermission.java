package com.example.demo.security.authorities;

public enum ApplicationUserPermission {
    FILE_UPLOAD("file:upload"),
    FILE_DOWNLOAD("file:download"),
    FILE_DELETE("file:delete");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}