package com.feblic.secure.models.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feblic.secure.constants.user.UserActivationStatus;
import com.feblic.secure.constants.user.UserVerificationStatus;
import com.feblic.secure.models.BaseEntityModel;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;

@Document(collection = "users")
public class UserModel extends BaseEntityModel {
    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @NotNull
    @Size(min = 8, message = "Name should have at least 8 characters")
    private String name;

    @NotNull
    @Min(18)
    private int age;

    @NotNull
    @Unique
    @Email(message = "Should be a valid email address")
    private String email;

    @NotNull
    @Pattern(regexp = "^((\\+)91)[1-9][0-9]{9}$", message = "Should be a valid mobile number")
    private String mobile;

    @NotNull
    private String password;

    @Field("user_activation_status")
    @JsonProperty("user_activation_status")
    private UserActivationStatus userActivationStatus;

    @Field("user_verification_status")
    @JsonProperty("user_verification_status")
    private UserVerificationStatus userVerificationStatus;

    @Override
    public String getSequenceName() {
        return SEQUENCE_NAME;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserActivationStatus getUserActivationStatus() {
        return userActivationStatus;
    }

    public void setUserActivationStatus(UserActivationStatus userActivationStatus) {
        this.userActivationStatus = userActivationStatus;
    }

    public UserVerificationStatus getUserVerificationStatus() {
        return userVerificationStatus;
    }

    public void setUserVerificationStatus(UserVerificationStatus userVerificationStatus) {
        this.userVerificationStatus = userVerificationStatus;
    }
}
