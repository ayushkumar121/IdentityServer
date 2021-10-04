package com.feblic.secure.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feblic.secure.constants.auth.AuthPermissionType;
import org.springframework.data.mongodb.core.mapping.Field;
import javax.validation.constraints.NotNull;

public class AuthPermission {

    @NotNull
    @Field("permission_type")
    @JsonProperty("permission_type")
    private AuthPermissionType permissionType;

    @NotNull
    @Field("permission_name")
    @JsonProperty("permission_name")
    private String permissionName;

    public AuthPermission() {}

    public AuthPermission(AuthPermissionType permissionType, String permissionName) {
        this.permissionType = permissionType;
        this.permissionName = permissionName;
    }

    public AuthPermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(AuthPermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}
