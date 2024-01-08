package pw.react.backend.security.common;

import org.springframework.security.core.userdetails.*;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.models.UserEntity;

import java.util.Optional;

public class CommonUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CommonUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
