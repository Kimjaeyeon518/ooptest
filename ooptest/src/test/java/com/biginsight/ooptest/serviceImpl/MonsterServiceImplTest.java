package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;
import com.biginsight.ooptest.repository.MonsterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MonsterServiceImplTest {

    @InjectMocks
    private MonsterServiceImpl monsterService;

    @Mock
    private MonsterRepository monsterRepository;

    private Monster monster;

    @BeforeEach
    public void initMonster() {
        monster = buildMonster(1l, 100F);
    }

    @DisplayName("몬스터 추가")
    @Test
    public void addMonster() {
        // given
        given(monsterRepository.save(any(Monster.class))).willReturn(monster);

        // when
        Monster savedMonster = monsterService.addMonster(monster);

        // then
        then(monsterRepository).should(times(1)).save(monster);
        assertThat(savedMonster).isEqualTo(monster);
    }

    @DisplayName("몬스터가 공격당함")
    @Test
    public void monsterUnderattack() {
        // given
        Monster underattackMonster = buildMonster(2l, 80F);
        given(monsterRepository.save(any(Monster.class))).willReturn(underattackMonster);
        given(monsterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(monster));

        // when
        Monster savedMonster = monsterService.addMonster(monster);
        MonsterResponseDto monsterResponseDto = monsterService.underattack(monster.getId(), 35F);

        // then
        then(monsterRepository).should(times(2)).save(any(Monster.class));
        assertThat(monsterResponseDto.getHp()).isEqualTo(underattackMonster.getHp());
    }

    private Monster buildMonster(Long id, Float hp) {
        return Monster.builder()
                .id(id)
                .hp(hp)
                .attackPower(20F)
                .defensePower(15F)
                .counterattackRate(30F) // 반격 확률 Default = 30%
                .build();
    }
}