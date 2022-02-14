package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.dto.request.GameCharacterRequestDto;
import com.biginsight.ooptest.repository.GameCharacterRepository;
import com.biginsight.ooptest.repository.WeaponRepository;
import com.biginsight.ooptest.service.GameCharacterService;
import com.biginsight.ooptest.service.WeaponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class GameCharacterServiceImpl implements GameCharacterService {

    private final GameCharacterRepository gameCharacterRepository;
    private final WeaponRepository weaponRepository;


    @Override
    public GameCharacter addGameCharacter(GameCharacter gameCharacter) {
        return gameCharacterRepository.save(gameCharacter);
    }

    @Override
    public GameCharacter wearWeapon(Long gameCharacterId, Long weaponId) {
        GameCharacter gameCharacter = gameCharacterRepository.findById(gameCharacterId)
                .orElseThrow(NullPointerException::new);

        Weapon weapon = weaponRepository.findById(weaponId)
                .orElseThrow(NullPointerException::new);

        gameCharacter.wearWeapon(weapon);

        return gameCharacterRepository.save(gameCharacter);
    }
}
