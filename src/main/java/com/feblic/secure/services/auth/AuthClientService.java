package com.feblic.secure.services.auth;

import com.feblic.secure.models.auth.AuthClient;
import com.feblic.secure.repository.auth.AuthClientsRepository;
import com.feblic.secure.services.BaseModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthClientService extends BaseModelService<AuthClient> {

    @Autowired
    AuthClientsRepository authClientsRepository;

    public AuthClient findByClientIdAndClientSecret(String clientId, String clientSecret) {
        return authClientsRepository.findByClientIdAndClientSecret(clientId, clientSecret);
    }

    public AuthClient findByClientIdAndRedirectUri(String clientId, String redirectUri) {
        return authClientsRepository.findByClientIdAndRedirectUri(clientId, redirectUri);
    }

    @Override
    public MongoRepository<AuthClient, Long> getRepository() {
        return authClientsRepository;
    }

    @Override
    public Class<AuthClient> getTypeClass() {
        return AuthClient.class;
    }
}
