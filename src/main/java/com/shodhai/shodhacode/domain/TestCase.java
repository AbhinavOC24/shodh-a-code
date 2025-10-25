package com.shodhai.shodhacode.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    @Column(length = 4000)
    private String inputData;

    @Column(length = 4000)
    private String expectedOutput;

    private boolean sample; // whether this is a sample testcase
}
