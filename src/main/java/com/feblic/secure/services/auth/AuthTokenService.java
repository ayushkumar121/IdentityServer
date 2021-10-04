package com.feblic.secure.services.auth;

import com.feblic.secure.models.auth.AuthToken;
import com.feblic.secure.repository.auth.AuthTokenRepository;
import com.feblic.secure.services.BaseModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService extends BaseModelService<AuthToken> {

    @Autowired
    AuthTokenRepository authTokenRepository;

    public AuthToken findByToken(String token) {
        return authTokenRepository.findByToken(token);
    }

    @Override
    public MongoRepository<AuthToken, Long> getRepository() {
        return authTokenRepository;
    }

    @Override
    public Class<AuthToken> getTypeClass() {
        return AuthToken.class;
    }
}
