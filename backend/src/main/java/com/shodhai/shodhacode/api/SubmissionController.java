package com.shodhai.shodhacode.api;

import com.shodhai.shodhacode.domain.*;
import com.shodhai.shodhacode.repo.*;
import com.shodhai.shodhacode.judge.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionRepo submissionRepo;
    private final UserRepo userRepo;
    private final JudgeService judgeService;

    // POST /api/submissions
    @PostMapping
    public Submission submit(@RequestBody Submission submission) {

        UserAccount user = userRepo.findByUsername(submission.getUser().getUsername())
                .orElseGet(() -> userRepo.save(UserAccount.builder()
                        .username(submission.getUser().getUsername())
                        .build()));

        submission.setUser(user);
        submission.setStatus(Submission.Status.RUNNING);
        submission.setCreatedAt(Instant.now());
        submission.setUpdatedAt(Instant.now());
        submissionRepo.save(submission);


        CompletableFuture.runAsync(() -> judgeService.runJudge(submission));

        return submission;
    }

    // GET /api/submissions/{id}
    @GetMapping("/{id}")
    public Submission getStatus(@PathVariable Long id) {
        return submissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }
}
