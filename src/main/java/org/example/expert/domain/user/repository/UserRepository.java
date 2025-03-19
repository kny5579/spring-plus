package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT new org.example.expert.domain.user.dto.response.UserResponse(u.id, u.email, u.nickname)" +
            "FROM User u WHERE u.nickname = :nickname")
    List<UserResponse> findByNickname(@Param("nickname") String nickname);

    boolean existsByEmail(String email);
}
