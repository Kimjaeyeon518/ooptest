package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.Monster;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;

public interface MonsterService {

    Monster addMonster(Monster monster);
    MonsterResponseDto underattack(Long monsterId, Float underattackPower);
    Monster checkHp(Monster monster);
}
