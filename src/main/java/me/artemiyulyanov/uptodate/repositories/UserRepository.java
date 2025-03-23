package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}