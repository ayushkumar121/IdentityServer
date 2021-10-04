package com.feblic.secure.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feblic.secure.models.BaseEntityModel;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "auth_clients")
public class AuthClient extends BaseEntityModel {
    @Transient
    public static final String SEQUENCE_NAME = "auth_clients_sequence";

    @Field("client_id")
    @Unique
    @JsonProperty("client_id")
    private String clientId;

    @Field("client_secret")
    @JsonProperty("client_secret")
    private String clientSecret;

    @Field("redirect_uri")
    @JsonProperty("redirect_uri")
    private String redirectUri;

    @Override
    public String getSequenceName() {
        return SEQUENCE_NAME;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
