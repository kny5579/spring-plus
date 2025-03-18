package org.example.expert.domain.user;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserJdbcRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class UserSaveTest {

    @Autowired
    private UserJdbcRepository userJdbcRepository;

    @Test
    void 유저_데이터_백만건_생성_JDBC() {
        int batchSize = 10000;
        List<User> userList = new ArrayList<>();

        for (int i = 1; i <= 1_000_000; i++) {
            String randomNickname = i + "_user" + UUID.randomUUID().toString().substring(0, 5);
            userList.add(new User("test" + i + "@em.com", "pw", randomNickname, UserRole.ROLE_USER));
            if (i % batchSize == 0) {
                userJdbcRepository.saveAll(userList);
                userList.clear();
            }
        }
    }
}
