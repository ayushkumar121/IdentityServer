package com.feblic.secure.services.auth;

import com.feblic.secure.models.auth.AuthCode;
import com.feblic.secure.repository.auth.AuthCodesRepository;
import com.feblic.secure.services.BaseModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthCodeService extends BaseModelService<AuthCode> {

    @Autowired
    AuthCodesRepository authCodesRepository;

    public AuthCode findByAuthCode(String authCode) {
        return authCodesRepository.findByAuthCode(authCode);
    }

    @Override
    public MongoRepository<AuthCode, Long> getRepository() {
        return authCodesRepository;
    }

    @Override
    public Class<AuthCode> getTypeClass() {
        return AuthCode.class;
    }
}
