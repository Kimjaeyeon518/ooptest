package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
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
        monster = buildMonster();
    }

    @DisplayName("몬스터 추가")
    @Test
    public void addGameCharacter() {
        // given
        given(monsterRepository.save(any(Monster.class))).willReturn(monster);

        // when
        Monster savedMonster = monsterService.addMonster(monster);

        // then
        then(monsterRepository).should(times(1)).save(monster);
        assertThat(savedMonster).isEqualTo(monster);
    }

    private Monster buildMonster() {
        return Monster.builder()
                .id(1L)
                .hp(100F)
                .attackPower(20F)
                .defensePower(15F)
                .counterattackRate(30F) // 반격 확률 Default = 30%
                .build();
    }
}