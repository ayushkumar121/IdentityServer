package com.feblic.secure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feblic.secure.models.auth.AuthPermission;

import java.util.List;

public class TokensValidationDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("permissions")
    private List<AuthPermission> permissions;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<AuthPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<AuthPermission> permissions) {
        this.permissions = permissions;
    }
}
