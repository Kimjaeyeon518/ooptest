package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;

public interface GameCharacterService {

    GameCharacter addGameCharacter(GameCharacter gameCharacter);
    GameCharacter wearWeapon(Weapon weapon);
}
