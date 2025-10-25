package com.shodhai.shodhacode.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private String sourceCode;
    private String language;
    private Status status;
    private String verdictMessage;
    private Long timeMs;
    private Instant createdAt;
    private Instant updatedAt;

    public enum Status { PENDING, RUNNING, ACCEPTED, WRONG_ANSWER, RTE }
}
