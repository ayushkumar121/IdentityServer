package com.feblic.secure.repository;

import com.feblic.secure.models.users.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends MongoRepository<UserModel, Long> {
    public UserModel findByEmail(String email);
}
