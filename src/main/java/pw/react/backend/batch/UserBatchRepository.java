package pw.react.backend.batch;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.UserEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

class UserBatchRepository implements BatchRepository<UserEntity> {
    private final JdbcTemplate jdbcTemplate;
    UserBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Collection<UserEntity> insertAll(Collection<UserEntity> entities) {
        String sql = "INSERT INTO `USER_ENTITY` (NAME, LAST_NAME, EMAIL, PASSWORD) VALUES(?,?,?,?)";

        final var users = new ArrayList<>(entities);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserEntity user = users.get(i);
                ps.setString(1, user.getName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPassword());
            }
            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
        return users;
    }
}
