package pw.react.backend.services;

import pw.react.backend.models.UserEntity;

import java.util.Collection;

public interface UserService {
    UserEntity validateAndSave(UserEntity user);
    UserEntity updatePassword(UserEntity user, String password);
    UserEntity saveUnique(UserEntity user);
    Collection<UserEntity> batchSave(Collection<UserEntity> users);
}
