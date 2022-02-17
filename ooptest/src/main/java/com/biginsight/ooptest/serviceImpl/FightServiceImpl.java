package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;
import com.biginsight.ooptest.service.FightService;
import com.biginsight.ooptest.service.GameCharacterService;
import com.biginsight.ooptest.service.MonsterService;
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
public class FightServiceImpl implements FightService {

    private final MonsterService monsterService;
    private final GameCharacterService gameCharacterService;

    @Override
    public FightResponseDto monsterUnderattack(FightResponseDto fightResponseDto) {
        GameCharacter gameCharacter = fightResponseDto.getGameCharacter();
        GameCharacterResponseDto reflectedGameCharacterResponseDto = fightResponseDto.getGameCharacterResponseDto();
        Monster monster = fightResponseDto.getMonster();
        MonsterResponseDto reflectedMonsterResponseDto = fightResponseDto.getMonsterResponseDto();

        Float totalDamage = (reflectedGameCharacterResponseDto.getAttackPower() - reflectedMonsterResponseDto.getDefensePower());
        // 몬스터의 방어력이 받은 공격력보다 높을 경우
        if(totalDamage <= 0) {
            log.info("몬스터의 방어력이 받은 공격력보다 높아서 데미지를 입지 않았습니다 !");
            return fightResponseDto;
        }
        else {
            monster.setHp(monster.getHp() - totalDamage);
            // 공격으로 인해 몬스터가 사망했을 경우
            if(monsterIsDead(monster)) {
                monster.setHp(0F);
                GameCharacter levelUpGameCharacter = gameCharacterService.levelUp(gameCharacter);  // 캐릭터 레벨업
                log.info("캐릭터가 레벨업 하였습니다 ! 현재 레벨 : " + levelUpGameCharacter.getLevel());
                monsterService.save(monster);
//                throw new ApiException(ApiErrorCode.MONSTER_IS_DEAD);
                return null;
            }
            log.info("몬스터가 공격당하였습니다 ! 최종 데미지 : " + totalDamage + ", 몬스터 현재 HP : " + monster.getHp());
        }

        // 몬스터가 반격한 경우
        if(new Random().nextInt(100) < reflectedMonsterResponseDto.getCounterattackRate()){
            log.info("몬스터가 반격했습니다 !");
            reflectedMonsterResponseDto.setAttackPower(reflectedMonsterResponseDto.getAttackPower() * 0.7F);  // 반격 데미지는 원래 공격력의 70%
            gameCharacterUnderattack(fightResponseDto);
        }

        Monster savedMonster = monsterService.save(monster);
        fightResponseDto.setMonster(savedMonster);
        fightResponseDto.setMonsterResponseDto(buildMonsterResponseDto(savedMonster));

        // 무기/스킬 효과 초기화
        fightResponseDto.setGameCharacterResponseDto(buildGameCharacterResponseDto(gameCharacter
                , reflectedGameCharacterResponseDto.getSkill()
                , reflectedGameCharacterResponseDto.getSkillExpiredDate()));

        return fightResponseDto;
    }

    @Override
    public FightResponseDto monsterDoAttack(FightResponseDto fightResponseDto) throws InterruptedException {
        // 몬스터는 3초에 1번 공격
        Thread.sleep(3000);

        return gameCharacterUnderattack(fightResponseDto);
    }

    @Override
    public FightResponseDto gameCharacterUnderattack(FightResponseDto fightResponseDto) {
        GameCharacter gameCharacter = fightResponseDto.getGameCharacter();
        GameCharacterResponseDto reflectedGameCharacterResponseDto = fightResponseDto.getGameCharacterResponseDto();

        // ex - 회피율이 30% 일 때, 0~100 중 랜덤으로 뽑은 숫자가 30미만일 확률 == 30%
        if(new Random().nextInt(100) < reflectedGameCharacterResponseDto.getAvoidanceRate()){
            log.info("캐릭터가 공격을 회피하였습니다 !");
            return fightResponseDto;
        }
        else {
            Float totalDamage = (fightResponseDto.getMonsterResponseDto().getAttackPower() - reflectedGameCharacterResponseDto.getDefensePower());
            // 캐릭터의 방어력이 받은 공격력보다 높을 경우
            if(totalDamage <= 0) {
                log.info("캐릭터의 방어력이 받은 공격력보다 높아서 데미지를 입지 않았습니다 !");
                return fightResponseDto;
            }

            else {
                gameCharacter.setHp(gameCharacter.getHp() - totalDamage);
                // 공격으로 인해 캐릭터가 사망했을 경우
                if(gameCharacterIsDead(gameCharacter)) {
                    gameCharacter.setHp(0F);
                    gameCharacterService.save(gameCharacter);
//                    throw new ApiException(ApiErrorCode.GAMECHARACTER_IS_DEAD);
                    return null;
                }
                log.info("캐릭터가 공격당하였습니다 ! 받은 데미지 : " + totalDamage + ", 현재 HP : " + gameCharacter.getHp());
            }
        }

        GameCharacter savedGameCharacter = gameCharacterService.save(gameCharacter);
        fightResponseDto.setGameCharacter(savedGameCharacter);

        // 반격했을 경우 몬스터 공격력이 원래의 70% 로 설정되기 떄문에 이를 대비하여 원래대로 초기화
        fightResponseDto.setMonsterResponseDto(buildMonsterResponseDto(fightResponseDto.getMonster()));

        return fightResponseDto;
    }

    @Override
    public FightResponseDto gameCharacterDoAttack(FightResponseDto fightResponseDto) throws InterruptedException {
        GameCharacter gameCharacter = fightResponseDto.getGameCharacter();
        GameCharacterResponseDto reflectedGameCharacterResponseDto = fightResponseDto.getGameCharacterResponseDto();

        // 무기 효과 받기
        reflectedGameCharacterResponseDto.reflectWeapon(gameCharacter.getWeapon());

        // 사용중인 스킬이 있을 경우
        if(reflectedGameCharacterResponseDto.getSkill() != null) {
            Date date = new Date();
            System.out.println("date = " + date.getTime());
            System.out.println("reflectedGameCharacterResponseDto.getSkillExpiredDate() = " + reflectedGameCharacterResponseDto.getSkillExpiredDate());
            // 스킬의 유효시간이 지나지 않았을 경우
            if(reflectedGameCharacterResponseDto.getSkillExpiredDate() >= date.getTime()) {
                log.info("캐릭터가 사용중인 스킬 - {} 의 효과가 적용됩니다.", reflectedGameCharacterResponseDto.getSkill().getName());
                reflectedGameCharacterResponseDto.reflectSkill(reflectedGameCharacterResponseDto.getSkill());
            }
            // 스킬의 유효시간이 지났을 경우
            else {
                log.info("캐릭터가 사용중이던 스킬 - {} 의 지속시간이 종료되었습니다.", reflectedGameCharacterResponseDto.getSkill().getName());
                reflectedGameCharacterResponseDto.setSkill(null);    // 사용중인 스킬 초기화
            }
        }

        // 캐릭터 공격속도에 따른 딜레이 발생
        Thread.sleep(60000 / Long.valueOf(gameCharacter.getAttackSpeed()));
        return monsterUnderattack(fightResponseDto);
    }

    @Override
    public Boolean monsterIsDead(Monster monster) {
        if(monster.getHp() <= 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean gameCharacterIsDead(GameCharacter gameCharacter) {
        if(gameCharacter.getHp() <= 0) {
            return true;
        }
        return false;
    }

    private MonsterResponseDto buildMonsterResponseDto(Monster monster) {
        return MonsterResponseDto.builder()
                .id(monster.getId())
                .hp(monster.getHp())
                .attackPower(monster.getAttackPower())
                .defensePower(monster.getDefensePower())
                .counterattackRate(monster.getCounterattackRate()) // 반격 확률 Default = 30%
                .build();
    }

    private GameCharacterResponseDto buildGameCharacterResponseDto(GameCharacter savedGameCharacter, Skill skill, Long skillExpiredDate) {

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
}
