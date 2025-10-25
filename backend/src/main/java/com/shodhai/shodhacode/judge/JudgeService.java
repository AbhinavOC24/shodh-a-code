package com.shodhai.shodhacode.judge;

import com.shodhai.shodhacode.domain.*;
import com.shodhai.shodhacode.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
                    .filter(tc -> Objects.equals(tc.getProblem().getId(), problem.getId()))
                    .toList();


            String lang = submission.getLanguage().toLowerCase();
            String image, srcFile, buildCmd, runCmd;

            switch (lang) {
                case "java" -> {
                    image = "azul/zulu-openjdk:17";
                    srcFile = "Main.java";
                    buildCmd = "javac Main.java";                     
                    runCmd   = "timeout 3s java Main < input.txt";     
                }
                case "python" -> {
                    image = "python:3.11-alpine";
                    srcFile = "main.py";
                    buildCmd = null;                                   
                    runCmd   = "timeout 3s python3 main.py < input.txt";
                }
                case "cpp", "c++" -> {
                    image = "gcc:13.2.0";
                    srcFile = "main.cpp";
                    buildCmd = "g++ -O2 -std=c++17 -o main main.cpp";  
                    runCmd   = "timeout 3s ./main < input.txt";
                }
                default -> throw new RuntimeException("Unsupported language: " + lang);
            }


            Path tempDir = Files.createTempDirectory("judge_");
            Files.writeString(tempDir.resolve(srcFile), submission.getSourceCode(), StandardCharsets.UTF_8);


            if (buildCmd != null) {
                ProcessBuilder compilePB = new ProcessBuilder(
                        "docker","run","--rm","-m","256m","--cpus","1.0",
                        "-v", tempDir.toAbsolutePath()+":/app","-w","/app",
                        image,"sh","-c", buildCmd
                );
                Process cp = compilePB.start();

                String cOut = readAll(cp.getInputStream());
                String cErr = readAll(cp.getErrorStream());
                int cCode = cp.waitFor();

                if (cCode != 0) {
                    submission.setStatus(Submission.Status.CE);
                    submission.setVerdictMessage(truncate("Compilation Error:\n" + (cErr.isBlank()? cOut : cErr), 3800));
                    submission.setUpdatedAt(Instant.now());
                    submissionRepo.save(submission);
                    cleanup(tempDir);
                    return;
                }
            }


            for (TestCase tc : tests) {
                Files.writeString(tempDir.resolve("input.txt"), tc.getInputData(), StandardCharsets.UTF_8);

                ProcessBuilder runPB = new ProcessBuilder(
                        "docker","run","--rm","-m","256m","--cpus","1.0",
                        "-v", tempDir.toAbsolutePath()+":/app","-w","/app",
                        image,"sh","-c", runCmd
                );
                Process rp = runPB.start();

                String out = readAll(rp.getInputStream());
                String err = readAll(rp.getErrorStream());
                int rCode = rp.waitFor();

                if (rCode == 124) {
                    submission.setStatus(Submission.Status.TLE);
                    submission.setVerdictMessage("Time Limit Exceeded on test " + tc.getId());
                    submission.setUpdatedAt(Instant.now());
                    submissionRepo.save(submission);
                    cleanup(tempDir);
                    return;
                }
                if (rCode != 0) {
                    submission.setStatus(Submission.Status.RTE);
                    submission.setVerdictMessage(truncate("Runtime Error on test " + tc.getId() + ":\n" + (err.isBlank()? out : err), 3800));
                    submission.setUpdatedAt(Instant.now());
                    submissionRepo.save(submission);
                    cleanup(tempDir);
                    return;
                }


                String normOut = out.replaceAll("\\s+", " ").trim();
                String normExp = tc.getExpectedOutput().replaceAll("\\s+", " ").trim();

                if (!normOut.equals(normExp)) {
                    submission.setStatus(Submission.Status.WRONG_ANSWER);
                    submission.setVerdictMessage("Test " + tc.getId() + " failed. Expected: '" +
                            tc.getExpectedOutput().trim() + "', Got: '" + out.trim() + "'");
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
            submission.setVerdictMessage(truncate("Runtime error: " + e.getMessage(), 3800));
            submission.setUpdatedAt(Instant.now());
            submissionRepo.save(submission);
        }
    }

    private static String readAll(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static String truncate(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max);
    }

    private void cleanup(Path dir) {
        try {
            Files.walk(dir).sorted(Comparator.reverseOrder()).forEach(p -> {
                try { Files.delete(p); } catch (Exception ignored) {}
            });
        } catch (IOException ignored) {}
    }
}
