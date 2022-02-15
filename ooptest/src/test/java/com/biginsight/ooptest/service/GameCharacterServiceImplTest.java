package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.*;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterSkillResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.GameCharacterRepository;
import com.biginsight.ooptest.repository.GameCharacterSkillRepository;
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

import java.util.ArrayList;

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
    @Mock
    private GameCharacterSkillRepository gameCharacterSkillRepository;

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
        assertThat(wearWeaponGameCharacter.getAttackPower()).isEqualTo(gameCharacter.getAttackPower() * 105/100);
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
                () -> gameCharacterService.wearWeapon(savedGameCharacter.getId(), weapon.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.INVALID_SPECIES.getMessage());
    }

    @DisplayName("캐릭터 스킬 습득 성공")
    @Test
    public void GameCharacterGetSkillSuccess() {
        // given
        Skill skill = buildSkill(CharacterSpecies.HUMAN, 10F, 10);
        GameCharacterSkill gameCharacterSkill = buildGameCharacterSkill(gameCharacter, skill);

        skill.getGameCharacterSkillList().add(gameCharacterSkill);          // 양방향 매핑
        gameCharacter.getGameCharacterSkillList().add(gameCharacterSkill);  // 양방향 매핑

        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(skillRepository.save(any(Skill.class))).willReturn(skill);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(skill));
        given(gameCharacterSkillRepository.save(any(GameCharacterSkill.class))).willReturn(gameCharacterSkill);

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        Skill savedSkill = skillRepository.save(skill);
        GameCharacterSkillResponseDto gameCharacterSkillResponseDto = gameCharacterService.getSkill(savedGameCharacter.getId(), savedSkill.getId());

        // then
        then(gameCharacterRepository).should(times(2)).save(any(GameCharacter.class));
        then(skillRepository).should(times(2)).save(any(Skill.class));
        assertThat(gameCharacterSkillResponseDto).isNotNull();
        assertThat(gameCharacterSkillResponseDto.getGameCharacter()).isEqualTo(gameCharacter);
        assertThat(gameCharacterSkillResponseDto.getSkill()).isEqualTo(skill);
        assertThat(gameCharacterSkillResponseDto.getId()).isEqualTo(skill.getGameCharacterSkillList().get(0).getId());
        assertThat(gameCharacterSkillResponseDto.getId()).isEqualTo(gameCharacter.getGameCharacterSkillList().get(0).getId());
    }

    @DisplayName("캐릭터 스킬 습득 실패(레벨 부족)")
    @Test
    public void GameCharacterGetSkillFailedByLevel() {
        // given
        Skill skill = buildSkill(CharacterSpecies.HUMAN, 10F, 1000);

        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(skillRepository.save(any(Skill.class))).willReturn(skill);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(skill));

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        Skill savedSkill = skillRepository.save(skill);
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.getSkill(savedGameCharacter.getId(),savedSkill.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.NOT_ENOUGH_LEVEL.getMessage());
    }

    @DisplayName("캐릭터 스킬 습득 실패(종족 불일치)")
    @Test
    public void GameCharacterUseSkillFailedBySpecies() {
        // given
        Skill skill = buildSkill(CharacterSpecies.ELF, 10F, 10);
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(skill));

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.getSkill(savedGameCharacter.getId(),skill.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.INVALID_SPECIES.getMessage());
    }

    @DisplayName("캐릭터 스킬 사용 성공")
    @Test
    public void GameCharacterUseSkillSuccess() {
        // given
        Skill skill = buildSkill(CharacterSpecies.HUMAN, 10F, 10);
        GameCharacterSkill gameCharacterSkill = buildGameCharacterSkill(gameCharacter, skill);

        skill.getGameCharacterSkillList().add(gameCharacterSkill);          // 양방향 매핑
        gameCharacter.getGameCharacterSkillList().add(gameCharacterSkill);  // 양방향 매핑

        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(skill));
        given(skillRepository.save(any(Skill.class))).willReturn(skill);
        given(gameCharacterSkillRepository.save(any(GameCharacterSkill.class))).willReturn(gameCharacterSkill);
        given(gameCharacterSkillRepository.existsByGameCharacterIdAndSkillId(any(Long.class), any(Long.class))).willReturn(true);

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        Skill savedSkill = skillRepository.save(skill);
        GameCharacterSkillResponseDto gameCharacterSkillResponseDto = gameCharacterService.getSkill(savedGameCharacter.getId(), savedSkill.getId());
        GameCharacterResponseDto wearWeaponGameCharacter = gameCharacterService.useSkill(savedGameCharacter.getId(), skill.getId());

        // then
        then(gameCharacterRepository).should(times(3)).save(any(GameCharacter.class));
        assertThat(wearWeaponGameCharacter).isNotNull();
        assertThat(wearWeaponGameCharacter.getId()).isEqualTo(savedGameCharacter.getId());
        assertThat(gameCharacterSkillResponseDto.getSkill()).isEqualTo(skill);
        assertThat(gameCharacterSkillResponseDto.getId()).isEqualTo(skill.getGameCharacterSkillList().get(0).getId());
        assertThat(gameCharacterSkillResponseDto.getId()).isEqualTo(gameCharacter.getGameCharacterSkillList().get(0).getId());
    }

    @DisplayName("캐릭터 스킬 사용 실패(마나 부족)")
    @Test
    public void GameCharacterUseSkillFailedByMp() {
        // given
        Skill skill = buildSkill(CharacterSpecies.HUMAN, 1000F, 10);
        GameCharacterSkill gameCharacterSkill = buildGameCharacterSkill(gameCharacter, skill);

        skill.getGameCharacterSkillList().add(gameCharacterSkill);          // 양방향 매핑
        gameCharacter.getGameCharacterSkillList().add(gameCharacterSkill);  // 양방향 매핑

        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(skillRepository.save(any(Skill.class))).willReturn(skill);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(skill));
        given(gameCharacterSkillRepository.save(any(GameCharacterSkill.class))).willReturn(gameCharacterSkill);
        given(gameCharacterSkillRepository.existsByGameCharacterIdAndSkillId(any(Long.class), any(Long.class))).willReturn(true);

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        Skill savedSkill = skillRepository.save(skill);
        GameCharacterSkillResponseDto gameCharacterSkillResponseDto = gameCharacterService.getSkill(savedGameCharacter.getId(), savedSkill.getId());
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.useSkill(savedGameCharacter.getId(),savedSkill.getId()));

        // then
        then(gameCharacterRepository).should(times(2)).save(any(GameCharacter.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.NOT_ENOUGH_MP.getMessage());
        assertThat(gameCharacterSkillResponseDto.getSkill()).isEqualTo(skill);
        assertThat(gameCharacterSkillResponseDto.getId()).isEqualTo(skill.getGameCharacterSkillList().get(0).getId());
        assertThat(gameCharacterSkillResponseDto.getId()).isEqualTo(gameCharacter.getGameCharacterSkillList().get(0).getId());
    }

    @DisplayName("캐릭터 스킬 사용 실패(미습득)")
    @Test
    public void GameCharacterUseSkillFailedByNotGet() {
        // given
        Skill skill = buildSkill(CharacterSpecies.HUMAN, 10F, 10);
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(skill));

        // when
        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.useSkill(savedGameCharacter.getId(),skill.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.CANNOT_FOUND_SKILL.getMessage());
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
                .gameCharacterSkillList(new ArrayList<>())
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
                .duration(10)
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