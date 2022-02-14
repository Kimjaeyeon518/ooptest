package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GameCharacterRepositoryTest {

    @Mock
    private GameCharacterRepository gameCharacterRepository;
    private GameCharacter gameCharacter;

    @BeforeEach
    public void initGameCharacter() {
        gameCharacter = buildHuman(buildDefaultWeapon());
    }

    @DisplayName("캐릭터 추가")
    @Test
    public void addGameCharacter() {
        // given
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);

        // when
        GameCharacter savedGameCharacter = gameCharacterRepository.save(gameCharacter);

        // then
        then(gameCharacterRepository).should(times(1)).save(gameCharacter);
        assertThat(savedGameCharacter).isEqualTo(gameCharacter);
    }

    @DisplayName("캐릭터 조회")
    @Test
    public void findGameCharacter() {
        // given
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(gameCharacter));

        // when
        GameCharacter savedGameCharacter = gameCharacterRepository.save(gameCharacter);
        GameCharacter foundGameCharacter = gameCharacterRepository.findById(gameCharacter.getId()).get();

        // then
        then(gameCharacterRepository).should(times(1)).save(gameCharacter);
        then(gameCharacterRepository).should(times(1)).findById(gameCharacter.getId());
        assertThat(savedGameCharacter).isEqualTo(foundGameCharacter);
    }

    private GameCharacter buildHuman(Weapon weapon) {
        return GameCharacter.builder()
                .id(1L)
                .level(20)
                .hp(100F)
                .mp(100F)
                .attackPower(10F)
                .attackSpeed(30F)
                .defensePower(5F)
                .avoidanceRate(20F)
                .characterSpecies(CharacterSpecies.HUMAN)
                .weapon(weapon)
                .build();
    }

    private Weapon buildDefaultWeapon() {
        CharacterSpecies characterSpecies = CharacterSpecies.COMMON;
        String name = "default_weapon";
        String effect = "";

        return Weapon.builder()
                .characterSpecies(characterSpecies)
                .name(name)
                .effect(effect)
                .build();
    }
}