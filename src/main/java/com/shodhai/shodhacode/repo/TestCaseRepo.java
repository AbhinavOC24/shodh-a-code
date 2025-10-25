package com.shodhai.shodhacode.repo;

import com.shodhai.shodhacode.domain.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepo extends JpaRepository<TestCase, Long> {}
