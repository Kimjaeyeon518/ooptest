package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.repository.GameCharacterRepository;
import com.biginsight.ooptest.serviceImpl.GameCharacterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class GameCharacterServiceImplTest {

    @InjectMocks
    private GameCharacterServiceImpl gameCharacterService;

    @Mock
    private GameCharacterRepository gameCharacterRepository;


    private GameCharacter buildHuman(Weapon weapon) {
        return GameCharacter.builder()
                .id(1L)
                .level(1)
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
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);

        // then
        then(gameCharacterRepository).should(times(1)).save(gameCharacter);
        assertThat(savedGameCharacter).isEqualTo(gameCharacter);
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

    private Weapon buildHumanWeapon() {
        CharacterSpecies characterSpecies = CharacterSpecies.HUMAN;
        String name = "Short sword";
        String effect = "attackPower,+5";

        return Weapon.builder()
                .characterSpecies(characterSpecies)
                .name(name)
                .effect(effect)
                .build();
    }
}