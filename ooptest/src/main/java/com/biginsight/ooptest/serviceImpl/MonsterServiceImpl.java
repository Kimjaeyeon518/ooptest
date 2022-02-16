package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.MonsterRepository;
import com.biginsight.ooptest.service.MonsterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MonsterServiceImpl implements MonsterService {

    private final MonsterRepository monsterRepository;

    @Override
    public Monster addMonster(Monster monster) {
        return monsterRepository.save(monster);
    }

    @Override
    public MonsterResponseDto underattack(Long monsterId, Float underattackPower) {
        Monster findMonster = monsterRepository.findById(monsterId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER));

        // 몬스터가 반격한 경우
        if(new Random().nextInt(100) < findMonster.getCounterattackRate()){
            log.info("몬스터가 반격했습니다 !");
        }
        
        Float totalDamage = (underattackPower - findMonster.getDefensePower());
        // 몬스터의 방어력이 받은 공격력보다 높을 경우
        if(totalDamage <= 0)
            log.info("몬스터가 공격당하였습니다 ! 최종 데미지 : 0, 현재 HP : " + findMonster.getHp());
        else {
            findMonster.setHp(findMonster.getHp() - totalDamage);
            log.info("몬스터가 공격당하였습니다 ! 최종 데미지 : " + totalDamage + ", 현재 HP : " + findMonster.getHp());
        }


        // 몬스터 HP 상태 체크
        Monster checkedMonster = checkHp(findMonster);

        Monster savedMonster = monsterRepository.save(checkedMonster);

        return returnMonsterResponse(savedMonster);
    }

    @Override
    public Monster checkHp(Monster monster) {
        if(monster.getHp() <= 0) {
            monster.setHp(0F);
            log.info("몬스터가 죽었습니다.");
        }
        return monster;
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
