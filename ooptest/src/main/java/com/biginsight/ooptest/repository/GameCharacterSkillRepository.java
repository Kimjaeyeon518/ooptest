package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.GameCharacterSkill;
import com.biginsight.ooptest.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCharacterSkillRepository extends JpaRepository<GameCharacterSkill, Long> {
    boolean existsByGameCharacterIdAndSkillId(Long gameCharacterId, Long skillId);
}
