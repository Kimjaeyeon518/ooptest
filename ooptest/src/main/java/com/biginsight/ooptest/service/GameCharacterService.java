package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterSkillResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;

public interface GameCharacterService {

    GameCharacter addGameCharacter(GameCharacter gameCharacter);
    GameCharacter wearWeapon(Long gameCharacterId, Long weaponId);
    GameCharacter levelUp(GameCharacter gameCharacter);
    GameCharacterResponseDto useSkill(Long gameCharacterId, Long skillId);
    GameCharacterSkillResponseDto getSkill(Long gameCharacterId, Long skillId);
    FightResponseDto underattack(FightResponseDto fightResponseDto);
    Boolean isDead(GameCharacterResponseDto gameCharacterResponseDto);
    FightResponseDto doAttack(FightResponseDto fightResponseDto);
}
