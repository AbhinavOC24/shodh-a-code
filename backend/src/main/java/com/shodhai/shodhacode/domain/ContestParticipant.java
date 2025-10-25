package com.shodhai.shodhacode.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ContestParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Contest contest;

    @ManyToOne(optional = false)
    private UserAccount user;

    @Builder.Default
    private int totalScore = 0;


    @Column(length = 1000)
    private String solvedProblems;  


    public Set<Long> getSolvedProblemSet() {
        if (solvedProblems == null || solvedProblems.isBlank()) return new HashSet<>();
        return Stream.of(solvedProblems.split(","))
                .filter(s -> !s.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    public void addSolvedProblem(Long problemId) {
        Set<Long> solved = getSolvedProblemSet();
        solved.add(problemId);
        this.solvedProblems = solved.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
