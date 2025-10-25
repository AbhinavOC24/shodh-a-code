package com.shodhai.shodhacode.bootstrap;

import com.shodhai.shodhacode.domain.*;
import com.shodhai.shodhacode.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ContestRepo contestRepo;
    private final ProblemRepo problemRepo;
    private final TestCaseRepo testCaseRepo;

    @Override
    public void run(String... args) {
        if (contestRepo.findByCode("DEMO2025").isPresent()) return; // skip if already seeded

        // 1️⃣ Create contest
        var contest = contestRepo.save(
            Contest.builder()
                   .code("DEMO2025")
                   .title("Shodh-a-Code Demo Contest")
                   .startsAt(Instant.now().minusSeconds(3600))
                   .endsAt(Instant.now().plusSeconds(86400))
                   .build()
        );

        // 2️⃣ Problem 1 — Sum of Two
        var p1 = problemRepo.save(
            Problem.builder()
                   .contest(contest)
                   .code("SUM2")
                   .title("Sum of Two Numbers")
                   .statement("Read two integers and print their sum.")
                   .language("java")
                   .score(100)
                   .build()
        );
        testCaseRepo.saveAll(List.of(
            TestCase.builder().problem(p1).inputData("2 3\n").expectedOutput("5\n").build(),
            TestCase.builder().problem(p1).inputData("10 -10\n").expectedOutput("0\n").build(),
            TestCase.builder().problem(p1).inputData("100 250\n").expectedOutput("350\n").build()
        ));

        // 3️⃣ Problem 2 — Echo
        var p2 = problemRepo.save(
            Problem.builder()
                   .contest(contest)
                   .code("ECHO")
                   .title("Echo Line")
                   .statement("Read a line and print it back.")
                   .language("java")
                   .score(50)
                   .build()
        );
        testCaseRepo.saveAll(List.of(
            TestCase.builder().problem(p2).inputData("hello\n").expectedOutput("hello\n").build(),
            TestCase.builder().problem(p2).inputData("world\n").expectedOutput("world\n").build()
        ));
    }
}
