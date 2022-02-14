package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.dto.request.GameCharacterRequestDto;

public interface GameCharacterService {

    GameCharacter addGameCharacter(GameCharacter gameCharacter);
    GameCharacter wearWeapon(Long gameCharacterId, Long weaponId);
}
