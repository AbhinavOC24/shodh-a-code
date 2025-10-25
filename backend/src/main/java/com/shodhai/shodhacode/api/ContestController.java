package com.shodhai.shodhacode.api;

import com.shodhai.shodhacode.domain.*;
import com.shodhai.shodhacode.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestRepo contestRepo;
    private final SubmissionRepo submissionRepo;


    // GET /api/contests/{contestId}
    @GetMapping("/{contestId}")
    public Contest getContest(@PathVariable Long contestId) {
        return contestRepo.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
    }

    // GET /api/contests/{contestId}/leaderboard
    @GetMapping("/{contestId}/leaderboard")
    public List<Map<String, Object>> leaderboard(@PathVariable Long contestId) {
        List<Submission> subs = submissionRepo.findByContestId(contestId);

        Map<String, Long> scores = new HashMap<>();
        for (Submission s : subs) {
            if (s.getStatus() == Submission.Status.ACCEPTED) {
                scores.merge(s.getUser().getUsername(),(long) s.getProblem().getScore(), Long::sum);
            }
        }

        List<Map<String, Object>> board = new ArrayList<>();
        scores.forEach((u, sc) -> {
            board.add(Map.of("username", u, "score", sc));
        });
        board.sort((a, b) -> Long.compare((Long)b.get("score"), (Long)a.get("score")));
        return board;
    }
}
