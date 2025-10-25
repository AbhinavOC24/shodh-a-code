package com.shodhai.shodhacode.repo;

import com.shodhai.shodhacode.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ContestParticipantRepo extends JpaRepository<ContestParticipant, Long> {
    List<ContestParticipant> findByContestId(Long contestId);
    Optional<ContestParticipant> findByContestIdAndUserId(Long contestId, Long userId);
}
