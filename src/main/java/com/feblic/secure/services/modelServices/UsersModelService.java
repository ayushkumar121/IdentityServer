package com.feblic.secure.services.modelServices;

import com.feblic.secure.models.users.UserModel;
import com.feblic.secure.repository.UsersRepository;
import com.feblic.secure.services.BaseModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class UsersModelService extends BaseModelService<UserModel> {

    @Autowired
    UsersRepository usersRepository;

    @Override
    public MongoRepository getRepository() {
        return usersRepository;
    }

    @Override
    public Class getTypeClass() {
        return UserModel.class;
    }

    public UserModel findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
}