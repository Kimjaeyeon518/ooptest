package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.*;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterSkillResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.*;
import com.biginsight.ooptest.service.GameCharacterService;
import com.biginsight.ooptest.service.MonsterService;
import com.biginsight.ooptest.service.SkillService;
import com.biginsight.ooptest.service.WeaponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameCharacterServiceImpl implements GameCharacterService {

    private final GameCharacterRepository gameCharacterRepository;
    private final GameCharacterSkillRepository gameCharacterSkillRepository;

    private final WeaponService weaponService;
    private final SkillService skillService;
    private final MonsterService monsterService;


    @Override
    public GameCharacter addGameCharacter(GameCharacter gameCharacter) {
        return gameCharacterRepository.save(gameCharacter);
    }

    @Override
    public GameCharacterResponseDto wearWeapon(Long gameCharacterId, Long weaponId) {
        GameCharacter findGameCharacter = gameCharacterRepository.findById(gameCharacterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        Weapon findWeapon = weaponService.findById(weaponId);

        // 캐릭터 종족과 무기 종족이 불일치할 경우
        if(!findGameCharacter.getCharacterSpecies().equals(findWeapon.getCharacterSpecies()))
            throw new ApiException(ApiErrorCode.INVALID_SPECIES);

        findGameCharacter.setWeapon(findWeapon);    // 무기 착용

        GameCharacter savedGameCharacter = gameCharacterRepository.save(findGameCharacter);
        
        return returnGameCharacterResponse(savedGameCharacter, null, 0);
    }

    @Override
    public GameCharacterResponseDto useSkill(Long gameCharacterId, Long skillId) {
        GameCharacter findGameCharacter = gameCharacterRepository.findById(gameCharacterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        Skill findSkill = skillService.findById(skillId);

        // 캐릭터가 습득한 스킬이 아닐 경우
        if(!gameCharacterSkillRepository.existsByGameCharacterIdAndSkillId(gameCharacterId, skillId))
            throw new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER_SKILL);

        // 캐릭터의 마나가 부족할 경우
        if (findGameCharacter.getMp() < findSkill.getRequiredMp())
            throw new ApiException(ApiErrorCode.NOT_ENOUGH_SKILL_MP);

        findGameCharacter.setMp(findGameCharacter.getMp() - findSkill.getRequiredMp());

        GameCharacter savedGameCharacter = gameCharacterRepository.save(findGameCharacter);

        return returnGameCharacterResponse(savedGameCharacter, findSkill, 0);
    }

    @Override
    public GameCharacterSkillResponseDto getSkill(Long gameCharacterId, Long skillId) {
        GameCharacter findGameCharacter = gameCharacterRepository.findById(gameCharacterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        Skill findSkill = skillService.findById(skillId);

        // 캐릭터 종족과 스킬 종족이 불일치할 경우
        if (!findGameCharacter.getCharacterSpecies().equals(findSkill.getCharacterSpecies()))
            throw new ApiException(ApiErrorCode.INVALID_SPECIES);

        // 캐릭터의 레벨이 스킬 사용 제한 레벨보다 낮을 경우
        if (findGameCharacter.getLevel() < findSkill.getRequiredLevel())
            throw new ApiException(ApiErrorCode.NOT_ENOUGH_SKILL_LEVEL);

        GameCharacterSkill gameCharacterSkill = GameCharacterSkill.builder()
                .gameCharacter(findGameCharacter)
                .skill(findSkill)
                .build();

        // 양방향매핑
        findSkill.getGameCharacterSkillList().add(gameCharacterSkill);
        findGameCharacter.getGameCharacterSkillList().add(gameCharacterSkill);

        // 캐릭터 스킬 습득
        GameCharacterSkill savedGameCharacterSkill = gameCharacterSkillRepository.save(gameCharacterSkill);
        gameCharacterRepository.save(findGameCharacter);
        skillService.save(findSkill);

        return returnGameCharacterSkillResponse(savedGameCharacterSkill);
    }

    @Override
    public GameCharacterResponseDto underattack(GameCharacterResponseDto gameCharacterResponseDto, Float underattackPower) {

        GameCharacter findGameCharacter = gameCharacterRepository.findById(gameCharacterResponseDto.getId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        // ex - 회피율이 30% 일 때, 0~100 중 랜덤으로 뽑은 숫자가 30미만일 확률 == 30%
        if(new Random().nextInt(100) < gameCharacterResponseDto.getAvoidanceRate()){
            log.info("캐릭터가 공격을 회피하였습니다 !");
            return gameCharacterResponseDto;
        }
        else {
            Float totalDamage = (underattackPower - gameCharacterResponseDto.getDefensePower());
            // 캐릭터의 방어력이 받은 공격력보다 높을 경우
            if(totalDamage <= 0) {
                log.info("캐릭터의 방어력이 받은 공격력보다 높아서 데미지를 입지 않았습니다 !");
                return gameCharacterResponseDto;
            }

            else {
                gameCharacterResponseDto.setHp(gameCharacterResponseDto.getHp() - totalDamage);
                // 공격으로 인해 캐릭터가 사망했을 경우
                if(isDead(gameCharacterResponseDto)) {
                    GameCharacter deadGameCharacter = gameCharacterResponseDto.toEntity();
                    gameCharacterRepository.save(deadGameCharacter);
                    throw new ApiException(ApiErrorCode.GAMECHARACTER_IS_DEAD);
                }
                log.info("캐릭터가 공격당하였습니다 ! 받은 데미지 : " + totalDamage + ", 현재 HP : " + gameCharacterResponseDto.getHp());
            }
        }
        GameCharacter savedGameCharacter = gameCharacterRepository.save(gameCharacterResponseDto.toEntity());

        return returnGameCharacterResponse(savedGameCharacter,
                gameCharacterResponseDto.getSkill(),
                gameCharacterResponseDto.getSkillExpiredDate());
    }

    @Override
    public Boolean isDead(GameCharacterResponseDto gameCharacterResponseDto) {
        if(gameCharacterResponseDto.getHp() <= 0) {
            gameCharacterResponseDto.setHp(0F);
            return true;
        }
        return false;
    }

//    @Override
//    public FightResponseDto gameCharacterAttack(FightResponseDto fightResponseDto) {
//
//        GameCharacter findGameCharacter = fightResponseDto.getGameCharacter();
//
//        if(findGameCharacter.getSkill)
//        Float totalAttackPower = fightResponseDto.getGameCharacter().getAttackPower();
//
//        monsterService.underattack(findMonster.getId(), findGameCharacter.getAttackPower());
//    }

    public GameCharacterResponseDto returnGameCharacterResponse(GameCharacter savedGameCharacter, Skill skill, long skillExpiredDate) {
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
                .skill(skill)     // 캐릭터가 현재 사용중인 스킬
                .skillExpiredDate(skillExpiredDate)   // 캐릭터가 현재 사용중인 스킬의 유효기간
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
