package pw.react.backend.web;

import jakarta.validation.constraints.Email;
import pw.react.backend.models.UserEntity;

public record UserDto(Long id, String username, String password, @Email String email) {

    public static UserDto valueFrom(UserEntity user) {
        return new UserDto(user.getId(), user.getUsername(), null, user.getEmail());
    }

    public static UserEntity convertToUser(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setId(userDto.id());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
        return user;
    }
}
