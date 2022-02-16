package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.Skill;

public interface SkillService {
    Skill save(Skill skill);
    Skill findById(Long skillId);
}
