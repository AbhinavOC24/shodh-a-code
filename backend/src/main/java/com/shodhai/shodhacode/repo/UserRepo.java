package com.shodhai.shodhacode.repo;

import com.shodhai.shodhacode.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
}
