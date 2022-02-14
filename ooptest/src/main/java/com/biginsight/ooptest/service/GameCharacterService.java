package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.dto.request.GameCharacterRequestDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;

public interface GameCharacterService {

    GameCharacter addGameCharacter(GameCharacter gameCharacter);
    GameCharacterResponseDto wearWeapon(Long gameCharacterId, Long weaponId);
    GameCharacterResponseDto useSkill(Long gameCharacterId, Long skillId);
}
