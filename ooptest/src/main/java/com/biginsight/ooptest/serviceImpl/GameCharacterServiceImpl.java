package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.*;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterSkillResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;
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
import java.util.Date;
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
    public GameCharacter wearWeapon(Long gameCharacterId, Long weaponId) {
        GameCharacter findGameCharacter = gameCharacterRepository.findById(gameCharacterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        Weapon findWeapon = weaponService.findById(weaponId);

        // 캐릭터 종족과 무기 종족이 불일치할 경우
        if(!findGameCharacter.getCharacterSpecies().equals(findWeapon.getCharacterSpecies()))
            throw new ApiException(ApiErrorCode.INVALID_SPECIES);

        findGameCharacter.setWeapon(findWeapon);    // 무기 착용

        return gameCharacterRepository.save(findGameCharacter);
    }

    @Override
    public GameCharacter levelUp(GameCharacter gameCharacter) {
        gameCharacter.setLevel(gameCharacter.getLevel() + 1);
        return gameCharacterRepository.save(gameCharacter);
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

        return returnGameCharacterResponse(savedGameCharacter, findSkill);
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
    public FightResponseDto underattack(FightResponseDto fightResponseDto) {
        GameCharacterResponseDto originalGameCharacterResponseDto = fightResponseDto.getOriginalGameCharacterResponseDto();
        GameCharacterResponseDto reflectedGameCharacterResponseDto = fightResponseDto.getReflectedGameCharacterResponseDto();

        // ex - 회피율이 30% 일 때, 0~100 중 랜덤으로 뽑은 숫자가 30미만일 확률 == 30%
        if(new Random().nextInt(100) < reflectedGameCharacterResponseDto.getAvoidanceRate()){
            log.info("캐릭터가 공격을 회피하였습니다 !");
            return fightResponseDto;
        }
        else {
            Float totalDamage = (fightResponseDto.getReflectedMonsterResponseDto().getAttackPower() - reflectedGameCharacterResponseDto.getDefensePower());
            // 캐릭터의 방어력이 받은 공격력보다 높을 경우
            if(totalDamage <= 0) {
                log.info("캐릭터의 방어력이 받은 공격력보다 높아서 데미지를 입지 않았습니다 !");
                return fightResponseDto;
            }

            else {
                reflectedGameCharacterResponseDto.setHp(reflectedGameCharacterResponseDto.getHp() - totalDamage);
                // 공격으로 인해 캐릭터가 사망했을 경우
                if(isDead(reflectedGameCharacterResponseDto)) {
                    GameCharacter deadGameCharacter = originalGameCharacterResponseDto.toEntity();
                    gameCharacterRepository.save(deadGameCharacter);
                    throw new ApiException(ApiErrorCode.GAMECHARACTER_IS_DEAD);
                }
                log.info("캐릭터가 공격당하였습니다 ! 받은 데미지 : " + totalDamage + ", 현재 HP : " + reflectedGameCharacterResponseDto.getHp());
            }
        }
        gameCharacterRepository.save(originalGameCharacterResponseDto.toEntity());
        fightResponseDto.setOriginalGameCharacterResponseDto(originalGameCharacterResponseDto);

        return fightResponseDto;
    }

    @Override
    public Boolean isDead(GameCharacterResponseDto gameCharacterResponseDto) {
        if(gameCharacterResponseDto.getHp() <= 0) {
            gameCharacterResponseDto.setHp(0F);
            return true;
        }
        return false;
    }

    @Override
    public FightResponseDto doAttack(FightResponseDto fightResponseDto) {

        GameCharacterResponseDto originalGameCharacterResponseDto = fightResponseDto.getOriginalGameCharacterResponseDto();
        GameCharacterResponseDto reflectedGameCharacterResponseDto = fightResponseDto.getReflectedGameCharacterResponseDto();

        // 스킬 / 무기 효과를 받을 캐릭터 객체
        // 무기 효과 받기
        reflectedGameCharacterResponseDto.reflectWeapon(originalGameCharacterResponseDto.getWeapon());

        // 사용중인 스킬이 있을 경우
        if(originalGameCharacterResponseDto.getSkill() != null) {
            Date date = new Date();
            // 스킬의 유효시간이 지나지 않았을 경우
            if(originalGameCharacterResponseDto.getSkillExpiredDate() < date.getTime()) {
                log.info("캐릭터가 사용중인 스킬 - {} 의 효과가 적용됩니다.", originalGameCharacterResponseDto.getSkill().getName());
                reflectedGameCharacterResponseDto.reflectSkill(originalGameCharacterResponseDto.getSkill());
            }
            // 스킬의 유효시간이 지났을 경우
            else {
                log.info("캐릭터가 사용중이던 스킬 - {} 의 지속시간이 종료되었습니다.", originalGameCharacterResponseDto.getSkill().getName());
                originalGameCharacterResponseDto.setSkill(null);    // 사용중인 스킬 초기화
            }
        }

        return monsterService.underattack(fightResponseDto);
    }

    public GameCharacterResponseDto returnGameCharacterResponse(GameCharacter savedGameCharacter, Skill skill) {

        long skillExpiredDate = 0;

        if(skill != null) {
            Date date = new Date();
            skillExpiredDate = date.getTime() / 1000L + skill.getDuration();     // 밀리세컨까지는 필요없으므로 1000으로 나눔 + 스킬의 지속시간(초)
        }

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
