package com.feblic.secure.repository.auth;

import com.feblic.secure.models.auth.AuthCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCodesRepository extends MongoRepository<AuthCode, Long> {
    public AuthCode findByAuthCode(String authCode);
}

