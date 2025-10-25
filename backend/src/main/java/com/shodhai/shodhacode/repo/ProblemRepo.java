package com.shodhai.shodhacode.repo;

import com.shodhai.shodhacode.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ProblemRepo extends JpaRepository<Problem, Long> {
    Optional<Problem> findByContestIdAndCode(Long contestId, String code);
    List<Problem> findByContestId(Long contestId);
}
