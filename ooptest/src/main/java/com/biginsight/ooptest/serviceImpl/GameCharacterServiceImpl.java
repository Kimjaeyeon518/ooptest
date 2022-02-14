package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.dto.request.GameCharacterRequestDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.GameCharacterRepository;
import com.biginsight.ooptest.repository.SkillRepository;
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
    private final SkillRepository skillRepository;


    @Override
    public GameCharacter addGameCharacter(GameCharacter gameCharacter) {
        return gameCharacterRepository.save(gameCharacter);
    }

    @Override
    public GameCharacterResponseDto wearWeapon(Long gameCharacterId, Long weaponId) {
        GameCharacter findGameCharacter = gameCharacterRepository.findById(gameCharacterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        Weapon findWeapon = weaponRepository.findById(weaponId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_WEAPON));

        // 캐릭터 종족과 무기 종족이 불일치할 경우
        if(!findGameCharacter.getCharacterSpecies().equals(findWeapon.getCharacterSpecies()))
            throw new ApiException(ApiErrorCode.INVALID_WEAPON_SPECIES);

        GameCharacter gameCharacter = GameCharacter.builder()
                .id(findGameCharacter.getId())
                .level(findGameCharacter.getLevel())
                .hp(findGameCharacter.getHp())
                .mp(findGameCharacter.getMp())
                .attackPower(findGameCharacter.getAttackPower())
                .attackSpeed(findGameCharacter.getAttackSpeed())
                .defensePower(findGameCharacter.getDefensePower())
                .avoidanceRate(findGameCharacter.getAvoidanceRate())
                .characterSpecies(findGameCharacter.getCharacterSpecies())
                .weapon(findWeapon)
                .build();

        GameCharacter savedGameCharacter = gameCharacterRepository.save(gameCharacter);

        return GameCharacterResponseDto.builder()
                .id(savedGameCharacter.getId())
                .level(savedGameCharacter.getLevel())
                .hp(savedGameCharacter.getHp())
                .mp(savedGameCharacter.getMp())
                .attackPower(savedGameCharacter.getAttackPower())
                .attackSpeed(savedGameCharacter.getAttackSpeed())
                .defensePower(savedGameCharacter.getDefensePower())
                .avoidanceRate(savedGameCharacter.getAvoidanceRate())
                .characterSpecies(savedGameCharacter.getCharacterSpecies())
                .weapon(savedGameCharacter.getWeapon())
                .build();
    }
}
