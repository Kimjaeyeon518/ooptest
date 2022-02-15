package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MonsterRepositoryTest {

    @Mock
    private MonsterRepository monsterRepository;
    private Monster monster;

    @BeforeEach
    public void initMonster() {
        monster = buildMonster();
    }

    @DisplayName("몬스터 생성")
    @Test
    public void addMonster() {
        // given
        given(monsterRepository.save(any(Monster.class))).willReturn(monster);

        // when
        Monster savedMonster = monsterRepository.save(monster);

        // then
        then(monsterRepository).should(times(1)).save(monster);
        assertThat(savedMonster).isEqualTo(monster);
    }

    @DisplayName("몬스터 조회")
    @Test
    public void findMonster() {
        // given
        given(monsterRepository.save(any(Monster.class))).willReturn(monster);
        given(monsterRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(monster));

        // when
        Monster savedMonster = monsterRepository.save(monster);
        Monster foundMonster = monsterRepository.findById(monster.getId()).get();

        // then
        then(monsterRepository).should(times(1)).save(monster);
        then(monsterRepository).should(times(1)).findById(monster.getId());
        assertThat(savedMonster).isEqualTo(foundMonster);
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