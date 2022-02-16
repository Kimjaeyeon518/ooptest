package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.MonsterRepository;
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
public class MonsterServiceImpl implements MonsterService {

    private final MonsterRepository monsterRepository;

    private final GameCharacterService gameCharacterService;

    @Override
    public Monster addMonster(Monster monster) {
        return monsterRepository.save(monster);
    }

    @Override
    public Monster findById(Long monsterId) {
        Monster monster = monsterRepository.findById(monsterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_MONSTER));

        return monster;
    }

    @Override
    public FightResponseDto underattack(FightResponseDto fightResponseDto) {
        MonsterResponseDto originalMonsterResponseDto = fightResponseDto.getOriginalMonsterResponseDto();
        MonsterResponseDto reflectedMonsterResponseDto = fightResponseDto.getReflectedMonsterResponseDto();
        
        Float totalDamage = (fightResponseDto.getReflectedGameCharacterResponseDto().getAttackPower() - reflectedMonsterResponseDto.getDefensePower());
        // 몬스터의 방어력이 받은 공격력보다 높을 경우
        if(totalDamage <= 0) {
            log.info("몬스터의 방어력이 받은 공격력보다 높아서 데미지를 입지 않았습니다 !");
            return fightResponseDto;
        }
        else {
            reflectedMonsterResponseDto.setHp(reflectedMonsterResponseDto.getHp() - totalDamage);
            // 공격으로 인해 몬스터가 사망했을 경우
            if(isDead(reflectedMonsterResponseDto)) {
                gameCharacterService.levelUp(fightResponseDto.getOriginalGameCharacterResponseDto().toEntity());  // 캐릭터 레벨업
                Monster deadMonster = originalMonsterResponseDto.toEntity();
                monsterRepository.save(deadMonster);
                throw new ApiException(ApiErrorCode.MONSTER_IS_DEAD);
            }
            log.info("몬스터가 공격당하였습니다 ! 최종 데미지 : " + totalDamage + ", 몬스터 현재 HP : " + reflectedMonsterResponseDto.getHp());
        }

        // 몬스터가 반격한 경우
        if(new Random().nextInt(100) < reflectedMonsterResponseDto.getCounterattackRate()){
            log.info("몬스터가 반격했습니다 !");
            reflectedMonsterResponseDto.setAttackPower(reflectedMonsterResponseDto.getAttackPower() * 0.7F);  // 반격 데미지는 원래 공격력의 70%
            gameCharacterService.underattack(fightResponseDto);
        }

        monsterRepository.save(originalMonsterResponseDto.toEntity());
        fightResponseDto.setOriginalMonsterResponseDto(originalMonsterResponseDto);

        return fightResponseDto;
    }

    @Override
    public Boolean isDead(MonsterResponseDto monsterResponseDto) {
        if(monsterResponseDto.getHp() <= 0) {
            monsterResponseDto.setHp(0F);
            return true;
        }
        return false;
    }

    @Override
    public FightResponseDto doAttack(FightResponseDto fightResponseDto) {
        return gameCharacterService.underattack(fightResponseDto);
    }

    public MonsterResponseDto returnMonsterResponse(Monster savedMonster) {
        return MonsterResponseDto.builder()
                .id(savedMonster.getId())
                .hp(savedMonster.getHp())
                .attackPower(savedMonster.getAttackPower())
                .defensePower(savedMonster.getDefensePower())
                .counterattackRate(savedMonster.getCounterattackRate())
                .build();
    }
}
