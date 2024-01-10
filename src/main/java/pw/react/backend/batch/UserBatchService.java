package pw.react.backend.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.models.UserEntity;
import pw.react.backend.services.UserMainService;

import java.util.Collection;
import java.util.Collections;

public class UserBatchService extends UserMainService {

    private static final Logger log = LoggerFactory.getLogger(UserBatchService.class);
    private final BatchRepository<UserEntity> batchRepository;

    public UserBatchService(UserRepository userRepository, PasswordEncoder passwordEncoder, BatchRepository<UserEntity> batchRepository) {
        super(userRepository, passwordEncoder);
        this.batchRepository = batchRepository;
    }

    @Override
    public Collection<UserEntity> batchSave(Collection<UserEntity> users) {
        log.info("Batch insert.");
        if (users != null && !users.isEmpty()) {
            Collection<UserEntity> insertedUsers = batchRepository.insertAll(users.stream()
                    .peek(it -> it.setPassword(passwordEncoder.encode(it.getPassword())))
                    .toList()
            );
            return userRepository.findAllByEmailIn(insertedUsers.stream().map(UserEntity::getEmail).toList());

        } else {
            log.warn("User collection is empty or null.");
            return Collections.emptyList();
        }
    }
}
