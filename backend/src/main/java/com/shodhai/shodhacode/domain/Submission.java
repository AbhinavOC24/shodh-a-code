package com.shodhai.shodhacode.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    @Column(length = 10000)
    private String sourceCode;

    private String language; 

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(length = 4000)
    private String verdictMessage;

    private Long timeMs;

    private Instant createdAt;
    private Instant updatedAt;

    public enum Status {
         RUNNING, ACCEPTED, WRONG_ANSWER, TLE, RTE, CE
    }
}
