package com.feblic.secure.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feblic.secure.models.BaseEntityModel;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "auth_codes")
public class AuthCode extends BaseEntityModel {
    @Transient
    public static final String SEQUENCE_NAME = "auth_codes_sequence";

    @Field("user_id")
    @JsonProperty("user_id")
    private Long userId;

    @Field("client_id")
    @JsonProperty("client_id")
    private String clientId;

    @Field("auth_client")
    @JsonProperty("auth_client")
    private AuthClient authClient;

    @Field("auth_code")
    @JsonProperty("auth_code")
    private String authCode;

    @Override
    public String getSequenceName() {
        return SEQUENCE_NAME;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AuthClient getAuthClient() {
        return authClient;
    }

    public void setAuthClient(AuthClient authClient) {
        this.authClient = authClient;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
