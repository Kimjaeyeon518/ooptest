package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
