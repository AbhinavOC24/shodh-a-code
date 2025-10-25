package com.shodhai.shodhacode.repo;

import com.shodhai.shodhacode.domain.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContestRepo extends JpaRepository<Contest, Long> {
    Optional<Contest> findByCode(String code);
}
