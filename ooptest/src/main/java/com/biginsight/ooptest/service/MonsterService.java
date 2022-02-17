package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.Monster;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;

public interface MonsterService {

    Monster save(Monster monster);
    Monster findById(Long monsterId);
    FightResponseDto underattack(FightResponseDto fightResponseDto);
    Boolean isDead(MonsterResponseDto monsterResponseDto);
    FightResponseDto doAttack(FightResponseDto fightResponseDto);
}
