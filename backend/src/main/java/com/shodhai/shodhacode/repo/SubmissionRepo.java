package com.shodhai.shodhacode.repo;

import com.shodhai.shodhacode.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findByContestId(Long contestId);
    List<Submission> findByContestIdAndUserId(Long contestId, Long userId);
}
