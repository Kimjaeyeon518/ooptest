package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
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
public class GameCharacterSkillRepositoryTest {

    @Mock
    private GameCharacterSkillRepository gameCharacterSkillRepository;
    private GameCharacterSkill gameCharacterSkill;

    @BeforeEach
    public void initGameCharacterSkill() {
        gameCharacterSkill = buildGameCharacterSkill(buildGameCharacter(1L, CharacterSpecies.HUMAN, buildWeapon(CharacterSpecies.HUMAN))
                , buildSkill(CharacterSpecies.HUMAN, 10F, 10));
    }

    @DisplayName("캐릭터가 스킬 습득")
    @Test
    public void addGameCharacterSkill() {
        // given
        given(gameCharacterSkillRepository.save(any(GameCharacterSkill.class))).willReturn(gameCharacterSkill);

        // when
        GameCharacterSkill savedGameCharacterSkill = gameCharacterSkillRepository.save(gameCharacterSkill);

        // then
        then(gameCharacterSkillRepository).should(times(1)).save(gameCharacterSkill);
        assertThat(savedGameCharacterSkill).isEqualTo(gameCharacterSkill);
    }

    @DisplayName("캐릭터가 습득한 스킬 조회")
    @Test
    public void findGameCharacterSkill() {
        // given
        given(gameCharacterSkillRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(gameCharacterSkill));

        // when
        GameCharacterSkill foundGameCharacterSkill = gameCharacterSkillRepository.findById(gameCharacterSkill.getId()).get();

        // then
        then(gameCharacterSkillRepository).should(times(1)).findById(gameCharacterSkill.getId());
        assertThat(gameCharacterSkill).isEqualTo(foundGameCharacterSkill);
    }

    private GameCharacter buildGameCharacter(Long id, CharacterSpecies characterSpecies, Weapon weapon) {
        return GameCharacter.builder()
                .id(id)
                .level(30)
                .hp(100F)
                .mp(100F)
                .attackPower(10F)
                .attackSpeed(30)
                .defensePower(5F)
                .avoidanceRate(30F)
                .characterSpecies(characterSpecies)
                .gameCharacterSkillList(new ArrayList<>())
                .weapon(weapon)
                .build();
    }

    private Weapon buildWeapon(CharacterSpecies characterSpecies) {
        String name = "Short sword";
        String effect = "attackPower,+5%";

        return Weapon.builder()
                .id(2L)
                .characterSpecies(characterSpecies)
                .name(name)
                .effect(effect)
                .build();
    }

    private Skill buildSkill(CharacterSpecies characterSpecies, Float requiredMp, Integer requiredLevel) {
        return Skill.builder()
                .id(1L)
                .characterSpecies(characterSpecies)
                .name("new skill")
                .requiredMp(requiredMp)
                .requiredLevel(requiredLevel)
                .gameCharacterSkillList(new ArrayList<>())
                .effect("attackSpeed,+10")
                .duration(10L)
                .build();
    }

    private GameCharacterSkill buildGameCharacterSkill(GameCharacter gameCharacter, Skill skill) {
        return GameCharacterSkill.builder()
                .id(1L)
                .gameCharacter(gameCharacter)
                .skill(skill)
                .build();
    }
}