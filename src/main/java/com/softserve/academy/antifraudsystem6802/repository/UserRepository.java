package com.softserve.academy.antifraudsystem6802.repository;



import com.softserve.academy.antifraudsystem6802.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    int deleteByUsernameIgnoreCase(String username);
}