package com.feblic.secure.repository.auth;

import com.feblic.secure.models.auth.AuthClient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthClientsRepository extends MongoRepository<AuthClient, Long> {
    public AuthClient findByClientIdAndClientSecret(String clientId, String clientSecret);
    public AuthClient findByClientIdAndRedirectUri(String clientId, String redirectUri);
}
