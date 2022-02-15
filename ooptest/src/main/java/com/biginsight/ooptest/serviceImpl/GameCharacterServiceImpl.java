package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.GameCharacterSkill;
import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterSkillResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.GameCharacterRepository;
import com.biginsight.ooptest.repository.GameCharacterSkillRepository;
import com.biginsight.ooptest.repository.SkillRepository;
import com.biginsight.ooptest.repository.WeaponRepository;
import com.biginsight.ooptest.service.GameCharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GameCharacterServiceImpl implements GameCharacterService {

    private final GameCharacterRepository gameCharacterRepository;
    private final WeaponRepository weaponRepository;
    private final SkillRepository skillRepository;
    private final GameCharacterSkillRepository gameCharacterSkillRepository;


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
            throw new ApiException(ApiErrorCode.INVALID_SPECIES);

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
                .weapon(findWeapon)     // 무기 착용
                .build();

        GameCharacter savedGameCharacter = gameCharacterRepository.save(gameCharacter);
        
        return returnGameCharacterResponse(savedGameCharacter);
    }

    @Override
    public GameCharacterResponseDto useSkill(Long gameCharacterId, Long skillId) {
        GameCharacter findGameCharacter = gameCharacterRepository.findById(gameCharacterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        Skill findSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_WEAPON));

        // 캐릭터가 습득한 스킬이 아닐 경우
        if(!gameCharacterSkillRepository.existsByGameCharacterIdAndSkillId(gameCharacterId, skillId))
            throw new ApiException(ApiErrorCode.CANNOT_FOUND_SKILL);

        // 캐릭터의 마나가 부족할 경우
        if (findGameCharacter.getMp() < findSkill.getRequiredMp())
            throw new ApiException(ApiErrorCode.NOT_ENOUGH_MP);

        GameCharacter gameCharacter = GameCharacter.builder()
                .id(findGameCharacter.getId())
                .level(findGameCharacter.getLevel())
                .hp(findGameCharacter.getHp())
                .mp(findGameCharacter.getMp() - findSkill.getRequiredMp())  // 스킬 사용 마나량만큼 감소
                .attackPower(findGameCharacter.getAttackPower())
                .attackSpeed(findGameCharacter.getAttackSpeed())
                .defensePower(findGameCharacter.getDefensePower())
                .avoidanceRate(findGameCharacter.getAvoidanceRate())
                .characterSpecies(findGameCharacter.getCharacterSpecies())
                .weapon(findGameCharacter.getWeapon())
                .build();

        GameCharacter savedGameCharacter = gameCharacterRepository.save(gameCharacter);

        return returnGameCharacterResponse(savedGameCharacter);
    }

    @Override
    public GameCharacterSkillResponseDto getSkill(Long gameCharacterId, Long skillId) {
        GameCharacter findGameCharacter = gameCharacterRepository.findById(gameCharacterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        Skill findSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_WEAPON));

        // 캐릭터 종족과 스킬 종족이 불일치할 경우
        if (!findGameCharacter.getCharacterSpecies().equals(findSkill.getCharacterSpecies()))
            throw new ApiException(ApiErrorCode.INVALID_SPECIES);

        // 캐릭터의 레벨이 스킬 사용 제한 레벨보다 낮을 경우
        if (findGameCharacter.getLevel() < findSkill.getRequiredLevel())
            throw new ApiException(ApiErrorCode.NOT_ENOUGH_LEVEL);

        GameCharacterSkill gameCharacterSkill = GameCharacterSkill.builder()
                .gameCharacter(findGameCharacter)
                .skill(findSkill)
                .build();

        // 양방향매핑
        findSkill.getGameCharacterSkillList().add(gameCharacterSkill);
        findGameCharacter.getGameCharacterSkillList().add(gameCharacterSkill);
        
        GameCharacterSkill savedGameCharacterSkill = gameCharacterSkillRepository.save(gameCharacterSkill);

        gameCharacterRepository.save(findGameCharacter);
        skillRepository.save(findSkill);

        return returnGameCharacterSkillResponse(savedGameCharacterSkill);
    }

    public GameCharacterResponseDto returnGameCharacterResponse(GameCharacter savedGameCharacter) {
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

    public GameCharacterSkillResponseDto returnGameCharacterSkillResponse(GameCharacterSkill savedGameCharacterSkill) {
        return GameCharacterSkillResponseDto.builder()
                .id(savedGameCharacterSkill.getId())
                .gameCharacter(savedGameCharacterSkill.getGameCharacter())
                .skill(savedGameCharacterSkill.getSkill())
                .build();
    }
}
