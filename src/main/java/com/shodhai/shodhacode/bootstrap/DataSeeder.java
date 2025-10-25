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
        if (contestRepo.count() > 0) return;

        // ✅ 1. Create contest
        Contest contest = Contest.builder()
                .title("Shodh-a-Code Demo Contest")
                .code("DEMO2025")
                .build();
        contestRepo.save(contest);

        // ✅ 2. Problem 1 — simple addition
        Problem p1 = Problem.builder()
                .title("A + B Problem")
                .code("ADD001")
                .statement("Read two integers and output their sum.")
                .language("java")
                .score(100)
                .contest(contest)
                .build();
        problemRepo.save(p1);

        // ✅ testcases for p1
        TestCase t1 = TestCase.builder()
                .inputData("2 3")
                .expectedOutput("5")
                .problem(p1)
                .build();

        TestCase t2 = TestCase.builder()
                .inputData("10 5")
                .expectedOutput("15")
                .problem(p1)
                .build();

        testCaseRepo.saveAll(List.of(t1, t2));

        // ✅ another sample problem
        Problem p2 = Problem.builder()
                .title("Multiply Numbers")
                .code("MUL002")
                .statement("Read two integers and output their product.")
                .language("java")
                .score(100)
                .contest(contest)
                .build();

        problemRepo.save(p2);

        TestCase t3 = TestCase.builder()
                .inputData("2 4")
                .expectedOutput("8")
                .problem(p2)
                .build();

        TestCase t4 = TestCase.builder()
                .inputData("3 5")
                .expectedOutput("15")
                .problem(p2)
                .build();

        testCaseRepo.saveAll(List.of(t3, t4));

        System.out.println("✅ Seeded contest + problems + testcases successfully!");
    }
}
