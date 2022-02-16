package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterSkillResponseDto;

public interface GameCharacterService {

    GameCharacter addGameCharacter(GameCharacter gameCharacter);
    GameCharacterResponseDto wearWeapon(Long gameCharacterId, Long weaponId);
    GameCharacterResponseDto useSkill(Long gameCharacterId, Long skillId);
    GameCharacterSkillResponseDto getSkill(Long gameCharacterId, Long skillId);
    GameCharacterResponseDto underattack(GameCharacterResponseDto gameCharacterResponseDto, Float underattackPower);
    Boolean isDead(GameCharacterResponseDto gameCharacterResponseDto);

//    FightResponseDto gameCharacterAttack(FightResponseDto fightResponseDto);
}
