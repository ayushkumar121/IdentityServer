package com.feblic.secure.repository.auth;

import com.feblic.secure.models.auth.AuthToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokenRepository extends MongoRepository<AuthToken, Long> {
    public AuthToken findByToken(String token);
}
