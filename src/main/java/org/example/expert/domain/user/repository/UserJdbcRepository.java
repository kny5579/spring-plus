package org.example.expert.domain.user.repository;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<User> userList) {
        jdbcTemplate.batchUpdate("insert into users (email, password, nickname, user_role, created_at, modified_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        User user = userList.get(i);
                        ps.setString(1, user.getEmail());
                        ps.setString(2, user.getPassword());
                        ps.setString(3, user.getNickname());
                        ps.setString(4, user.getUserRole().name());
                        ps.setTimestamp(5, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                        ps.setTimestamp(6, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                    }

                    @Override
                    public int getBatchSize() {
                        return userList.size();
                    }
                });
    }
}
