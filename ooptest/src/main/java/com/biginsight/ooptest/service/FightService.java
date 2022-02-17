package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;

public interface FightService {
    FightResponseDto monsterUnderattack(FightResponseDto fightResponseDto);
    FightResponseDto monsterDoAttack(FightResponseDto fightResponseDto) throws InterruptedException;
    FightResponseDto gameCharacterUnderattack(FightResponseDto fightResponseDto);
    FightResponseDto gameCharacterDoAttack(FightResponseDto fightResponseDto) throws InterruptedException;
    Boolean monsterIsDead(Monster monster);
    Boolean gameCharacterIsDead(GameCharacter gameCharacter);
}
