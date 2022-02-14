package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.dto.request.GameCharacterRequestDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
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
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        Weapon weapon = weaponRepository.findById(weaponId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_WEAPON));

        // 캐릭터 종족과 무기 종족이 불일치할 경우
        if(!gameCharacter.getCharacterSpecies().equals(weapon.getCharacterSpecies()))
            throw new ApiException(ApiErrorCode.INVALID_WEAPON_SPECIES);

        gameCharacter.wearWeapon(weapon);

        return gameCharacterRepository.save(gameCharacter);
    }
}
