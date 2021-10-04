package com.feblic.secure.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feblic.secure.constants.jwt.TokenType;
import com.feblic.secure.models.BaseEntityModel;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "auth_tokens")
public class AuthToken extends BaseEntityModel {
    @Transient
    public static final String SEQUENCE_NAME = "auth_tokens_sequence";

    @Field("user_id")
    @JsonProperty("user_id")
    private Long userId;

    @Field("client_id")
    @JsonProperty("client_id")
    private String clientId;

    @Field("auth_client")
    @JsonProperty("auth_client")
    private AuthClient authClient;

    @Field("token")
    @JsonProperty("token")
    private String token;

    @Field("token_type")
    @JsonProperty("token_type")
    private TokenType tokenType;

    @Field("validity")
    @JsonProperty("validity")
    @DateTimeFormat
    private Date validity;

    @Override
    public String getSequenceName() {
        return SEQUENCE_NAME;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthClient getAuthClient() {
        return authClient;
    }

    public void setAuthClient(AuthClient authClient) {
        this.authClient = authClient;
    }
}
