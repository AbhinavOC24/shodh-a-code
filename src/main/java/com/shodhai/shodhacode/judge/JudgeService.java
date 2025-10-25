package com.shodhai.shodhacode.judge;

import com.shodhai.shodhacode.domain.*;
import com.shodhai.shodhacode.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JudgeService {

    private final SubmissionRepo submissionRepo;
    private final ProblemRepo problemRepo;
    private final TestCaseRepo testCaseRepo;

    public void runJudge(Submission submission) {
        try {
            Problem problem = problemRepo.findById(submission.getProblem().getId())
                    .orElseThrow(() -> new RuntimeException("Problem not found"));

            List<TestCase> tests = testCaseRepo.findAll()
                    .stream()
                    .filter(tc -> tc.getProblem().getId().equals(problem.getId()))
                    .toList();

            // temp folder
            Path tempDir = Files.createTempDirectory("judge_");
            Path sourceFile = tempDir.resolve("Main.java");
            Files.writeString(sourceFile, submission.getSourceCode());

            // docker build/run
            String containerName = "judge_" + submission.getId();
            String image = "azul/zulu-openjdk:17";
            for (TestCase tc : tests) {
                ProcessBuilder pb = new ProcessBuilder(
                        "docker", "run", "--rm",
                        "-v", tempDir.toAbsolutePath() + ":/app",
                        "-w", "/app",
                        image,
                        "sh", "-c",
                        String.format("javac Main.java && echo '%s' | java Main", tc.getInputData().replace("\n", "\\n"))
                );
                pb.redirectErrorStream(true);
                Process p = pb.start();

                String output = new String(p.getInputStream().readAllBytes());
                p.waitFor();

                if (!output.strip().equals(tc.getExpectedOutput().strip())) {
                    submission.setStatus(Submission.Status.WRONG_ANSWER);
                    submission.setVerdictMessage("Expected: " + tc.getExpectedOutput() + ", Got: " + output);
                    submission.setUpdatedAt(Instant.now());
                    submissionRepo.save(submission);
                    cleanup(tempDir);
                    return;
                }
            }

            submission.setStatus(Submission.Status.ACCEPTED);
            submission.setVerdictMessage("All testcases passed!");
            submission.setUpdatedAt(Instant.now());
            submissionRepo.save(submission);

            cleanup(tempDir);

        } catch (Exception e) {
            submission.setStatus(Submission.Status.RTE);
            submission.setVerdictMessage("Runtime error: " + e.getMessage());
            submission.setUpdatedAt(Instant.now());
            submissionRepo.save(submission);
        }
    }

    private void cleanup(Path dir) {
        try {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.delete(p); } catch (Exception ignored) {}
                    });
        } catch (IOException ignored) {}
    }
}
