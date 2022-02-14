package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.dto.request.GameCharacterRequestDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.exception.CommonResponse;
import com.biginsight.ooptest.repository.GameCharacterRepository;
import com.biginsight.ooptest.repository.SkillRepository;
import com.biginsight.ooptest.repository.WeaponRepository;
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
    @Mock
    private WeaponRepository weaponRepository;
    @Mock
    private SkillRepository skillRepository;

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

    @DisplayName("캐릭터 무기 착용 성공")
    @Test
    public void GameCharacterWearsWeaponSuccess() {
        // given
        GameCharacter newWeaponHuman = buildHuman(buildWeapon(CharacterSpecies.HUMAN));
        Weapon weapon = buildWeapon(CharacterSpecies.HUMAN);
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(newWeaponHuman);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(newWeaponHuman));
        given(weaponRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(weapon));

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        GameCharacterResponseDto wearWeaponGameCharacter = gameCharacterService.wearWeapon(savedGameCharacter.getId(), weapon.getId());

        // then
        then(gameCharacterRepository).should(times(2)).save(any(GameCharacter.class));
        assertThat(wearWeaponGameCharacter).isNotNull();
        assertThat(wearWeaponGameCharacter.getId()).isEqualTo(savedGameCharacter.getId());
    }

    @DisplayName("캐릭터 무기 착용(변경) 실패(종족 불일치)")
    @Test
    public void GameCharacterWearsWeaponFailedBySpecies() {
        // given
        GameCharacter newWeaponHuman = buildHuman(buildWeapon(CharacterSpecies.OAK));
        Weapon weapon = buildWeapon(CharacterSpecies.OAK);
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(newWeaponHuman);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(newWeaponHuman));
        given(weaponRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(weapon));

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.wearWeapon(savedGameCharacter.getId(), buildWeapon(CharacterSpecies.OAK).getId()));

        // then
        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.INVALID_WEAPON_SPECIES.getMessage());
    }

    @DisplayName("캐릭터 스킬 사용 성공")
    @Test
    public void GameCharacterUseSkillSuccess() {
        // given
        Skill skill = buildSkill(CharacterSpecies.HUMAN);
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(skill));

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        GameCharacterResponseDto wearWeaponGameCharacter = gameCharacterService.useSkill(savedGameCharacter.getId(), skill.getId());

        // then
        then(gameCharacterRepository).should(times(2)).save(any(GameCharacter.class));
        assertThat(wearWeaponGameCharacter).isNotNull();
        assertThat(wearWeaponGameCharacter.getId()).isEqualTo(savedGameCharacter.getId());
    }

    private GameCharacter buildHuman(Weapon weapon) {
        return GameCharacter.builder()
                .id(1L)
                .level(30)
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
                .id(1L)
                .characterSpecies(characterSpecies)
                .name(name)
                .effect(effect)
                .build();
    }

    private Weapon buildWeapon(CharacterSpecies characterSpecies) {
        String name = "Short sword";
        String effect = "attackPower,-5%";

        return Weapon.builder()
                .id(2L)
                .characterSpecies(characterSpecies)
                .name(name)
                .effect(effect)
                .build();
    }

    private Skill buildSkill(CharacterSpecies characterSpecies) {
        return Skill.builder()
                .id(1L)
                .characterSpecies(characterSpecies)
                .name("new skill")
                .requiredMp(20F)
                .requiredLevel(10)
                .effect("attackSpeed,+10")
                .build();
    }
}