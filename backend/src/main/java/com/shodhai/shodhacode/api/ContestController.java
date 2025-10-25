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
    private final UserRepo userRepo;
    private final ContestParticipantRepo participantRepo;
    private final ProblemRepo problemRepo;

    //  GET /api/contests/{contestId}
    @GetMapping("/{contestId}")
    public Contest getContest(@PathVariable Long contestId) {
        return contestRepo.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
    }

    // GET /api/contests/{contestId}/leaderboard
    @GetMapping("/{contestId}/leaderboard")
    public List<Map<String, Object>> leaderboard(@PathVariable Long contestId) {
        List<ContestParticipant> participants = participantRepo.findByContestId(contestId);
    
        List<Map<String, Object>> board = new ArrayList<>();
    
        participants.stream()
                .filter(p -> p.getUser() != null)
                .sorted((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()))
                .forEach(p -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("username", p.getUser().getUsername());
                    entry.put("score", p.getTotalScore());
                    board.add(entry);
                });
    
        return board;
    }
    

    @PostMapping("/{contestId}/join")
    public Map<String, Object> joinContest(
        @PathVariable Long contestId,
        @RequestBody Map<String, Object> body
    )   {
    String username = (String) body.get("username");

    Contest contest = contestRepo.findById(contestId)
            .orElseThrow(() -> new RuntimeException("Contest not found"));


    UserAccount user = userRepo.findByUsername(username)
            .orElseGet(() -> {
                UserAccount u = new UserAccount();
                u.setUsername(username);
                return userRepo.save(u);
            });


    participantRepo.findByContestIdAndUserId(contestId, user.getId())
            .orElseGet(() -> participantRepo.save(
                    ContestParticipant.builder()
                            .contest(contest)
                            .user(user)
                            .totalScore(0)
                            .build()
            ));

    return Map.of(
            "joined", true,
            "user", user.getUsername(),
            "userId", user.getId()
    );
    }


    @PostMapping("/{contestId}/update-score")
public Map<String, Object> updateScore(
        @PathVariable Long contestId,
        @RequestBody Map<String, Object> body
) {
    Long userId = ((Number) body.get("userId")).longValue();
    Long problemId = ((Number) body.get("problemId")).longValue();

    Contest contest = contestRepo.findById(contestId)
            .orElseThrow(() -> new RuntimeException("Contest not found"));
    UserAccount user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    Problem problem = problemRepo.findById(problemId)
            .orElseThrow(() -> new RuntimeException("Problem not found"));

    ContestParticipant participant = participantRepo
            .findByContestIdAndUserId(contestId, userId)
            .orElseGet(() -> participantRepo.save(
                    ContestParticipant.builder()
                            .contest(contest)
                            .user(user)
                            .totalScore(0)
                            .solvedProblems("")
                            .build()
            ));

    Set<Long> solved = participant.getSolvedProblemSet();

    if (solved.contains(problemId)) {
        return Map.of(
                "updated", false,
                "message", "Problem already solved",
                "currentScore", participant.getTotalScore()
        );
    }


    participant.addSolvedProblem(problemId);
    participant.setTotalScore(participant.getTotalScore() + problem.getScore());
    participantRepo.saveAndFlush(participant);

    return Map.of(
            "updated", true,
            "newScore", participant.getTotalScore(),
            "solvedProblems", participant.getSolvedProblemSet()
    );
}

}
