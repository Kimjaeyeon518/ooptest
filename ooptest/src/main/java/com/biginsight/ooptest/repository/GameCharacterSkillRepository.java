package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.GameCharacterSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface GameCharacterSkillRepository extends JpaRepository<GameCharacterSkill, Long> {
}
