package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.repository.GameCharacterRepository;
import com.biginsight.ooptest.service.GameCharacterService;
import com.biginsight.ooptest.service.WeaponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class GameCharacterServiceImpl implements GameCharacterService {

    private final GameCharacterRepository gameCharacterRepository;


    @Override
    public GameCharacter addGameCharacter(GameCharacter gameCharacter) {
        return gameCharacterRepository.save(gameCharacter);
    }

    @Override
    public GameCharacter wearWeapon(Weapon weapon) {
        return null;
    }
}
