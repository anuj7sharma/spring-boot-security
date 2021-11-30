package com.learning.springSecurity.repository;

import com.learning.springSecurity.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u " +
            "SET u.enabled = true " +
            "WHERE u.email = ?1")
    void enableUser(String email);
}
