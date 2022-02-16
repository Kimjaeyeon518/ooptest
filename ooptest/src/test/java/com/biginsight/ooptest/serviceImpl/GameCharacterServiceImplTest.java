package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.*;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterSkillResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.*;
import com.biginsight.ooptest.service.MonsterService;
import com.biginsight.ooptest.service.SkillService;
import com.biginsight.ooptest.service.WeaponService;
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
    private GameCharacterSkillRepository gameCharacterSkillRepository;

    @Mock
    private WeaponService weaponSevice;

    @Mock
    private SkillService skillService;

    @Mock
    private MonsterService monsterService;

    private GameCharacter gameCharacter;
    private Weapon weapon;
    private Skill skill;
    private Monster monster;

    @BeforeEach
    public void initGameCharacter() {
        gameCharacter = buildGameCharacter(1L, CharacterSpecies.HUMAN, buildDefaultWeapon());
        weapon = buildWeapon(CharacterSpecies.HUMAN);
        skill = buildSkill(CharacterSpecies.HUMAN, 10F, 10);
        monster = buildMonster();
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
        GameCharacter wearWeaponGameCharacter = buildGameCharacter(2L, CharacterSpecies.HUMAN, buildWeapon(CharacterSpecies.HUMAN));
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(wearWeaponGameCharacter);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(wearWeaponGameCharacter));
        given(weaponSevice.findById(any(Long.class))).willReturn(weapon);

        // when
        GameCharacterResponseDto wearWeaponGameCharacterDto = gameCharacterService.wearWeapon(gameCharacter.getId(), weapon.getId());

        // then
        then(gameCharacterRepository).should(times(1)).findById(any(Long.class));
        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
        then(weaponSevice).should(times(1)).findById(any(Long.class));
        assertThat(wearWeaponGameCharacterDto).isNotNull();
        assertThat(wearWeaponGameCharacterDto.getWeapon()).isEqualTo(wearWeaponGameCharacter.getWeapon());
    }

    @DisplayName("캐릭터 무기 착용 실패(종족 불일치)")
    @Test
    public void GameCharacterWearsWeaponFailedBySpecies() {
        // given
        Weapon oakWeapon = buildWeapon(CharacterSpecies.OAK);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(weaponSevice.findById(any(Long.class))).willReturn(oakWeapon);

        // when
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.wearWeapon(gameCharacter.getId(), oakWeapon.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).findById(any(Long.class));
        then(weaponSevice).should(times(1)).findById(any(Long.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.INVALID_SPECIES.getMessage());
    }

    @DisplayName("캐릭터 스킬 습득 성공")
    @Test
    public void GameCharacterGetSkillSuccess() {
        // given
        GameCharacterSkill gameCharacterSkill = buildGameCharacterSkill(gameCharacter, skill);
        skill.getGameCharacterSkillList().add(gameCharacterSkill);          // 양방향 매핑
        gameCharacter.getGameCharacterSkillList().add(gameCharacterSkill);  // 양방향 매핑
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillService.findById(any(Long.class))).willReturn(skill);
        given(gameCharacterSkillRepository.save(any(GameCharacterSkill.class))).willReturn(gameCharacterSkill);

        // when
        GameCharacterSkillResponseDto gameCharacterSkillResponseDto = gameCharacterService.getSkill(gameCharacter.getId(), skill.getId());

        // then
        then(gameCharacterSkillRepository).should(times(1)).save(any(GameCharacterSkill.class));
        then(gameCharacterRepository).should(times(1)).findById(any(Long.class));
        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
        then(skillService).should(times(1)).save(any(Skill.class));
        then(skillService).should(times(1)).findById(any(Long.class));
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
        Skill highLevelSkill = buildSkill(CharacterSpecies.HUMAN, 10F, 1000);

        given(skillService.findById(any(Long.class))).willReturn(highLevelSkill);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));

        // when
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.getSkill(gameCharacter.getId(), highLevelSkill.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).findById(any(Long.class));
        then(skillService).should(times(1)).findById(any(Long.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.NOT_ENOUGH_SKILL_LEVEL.getMessage());
    }

    @DisplayName("캐릭터 스킬 습득 실패(종족 불일치)")
    @Test
    public void GameCharacterUseSkillFailedBySpecies() {
        // given
        Skill elfSkill = buildSkill(CharacterSpecies.ELF, 10F, 10);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillService.findById(any(Long.class))).willReturn(elfSkill);

        // when
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.getSkill(gameCharacter.getId(),elfSkill.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).findById(any(Long.class));
        then(skillService).should(times(1)).findById(any(Long.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.INVALID_SPECIES.getMessage());
    }

    @DisplayName("캐릭터 스킬 사용 성공")
    @Test
    public void GameCharacterUseSkillSuccess() {
        // given
        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillService.findById(any(Long.class))).willReturn(skill);
        given(gameCharacterSkillRepository.existsByGameCharacterIdAndSkillId(any(Long.class), any(Long.class))).willReturn(true);

        // when
        GameCharacterResponseDto useSkillGameCharacter = gameCharacterService.useSkill(gameCharacter.getId(), skill.getId());

        // then
        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
        then(gameCharacterRepository).should(times(1)).findById(any(Long.class));
        then(gameCharacterSkillRepository).should(times(1)).existsByGameCharacterIdAndSkillId(any(Long.class), any(Long.class));
        then(skillService).should(times(1)).findById(any(Long.class));
        assertThat(useSkillGameCharacter.getId()).isEqualTo(gameCharacter.getId());
        assertThat(useSkillGameCharacter.getSkill()).isEqualTo(skill);
    }

    @DisplayName("캐릭터 스킬 사용 실패(마나 부족)")
    @Test
    public void GameCharacterUseSkillFailedByMp() {
        // given
        Skill highMpSkill = buildSkill(CharacterSpecies.HUMAN, 1000F, 10);
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillService.findById(any(Long.class))).willReturn(highMpSkill);
        given(gameCharacterSkillRepository.existsByGameCharacterIdAndSkillId(any(Long.class), any(Long.class))).willReturn(true);

        // when
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.useSkill(gameCharacter.getId(), highMpSkill.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).findById(any(Long.class));
        then(gameCharacterSkillRepository).should(times(1)).existsByGameCharacterIdAndSkillId(any(Long.class), any(Long.class));
        then(skillService).should(times(1)).findById(any(Long.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.NOT_ENOUGH_SKILL_MP.getMessage());
    }

    @DisplayName("캐릭터 스킬 사용 실패(미습득)")
    @Test
    public void GameCharacterUseSkillFailedByNotGet() {
        // given
        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
        given(skillService.findById(any(Long.class))).willReturn(skill);
        given(gameCharacterSkillRepository.existsByGameCharacterIdAndSkillId(any(Long.class), any(Long.class))).willReturn(false);

        // when
        ApiException exception = assertThrows(ApiException.class,
                () -> gameCharacterService.useSkill(gameCharacter.getId(),skill.getId()));

        // then
        then(gameCharacterRepository).should(times(1)).findById(any(Long.class));
        then(gameCharacterSkillRepository).should(times(1)).existsByGameCharacterIdAndSkillId(any(Long.class), any(Long.class));
        then(skillService).should(times(1)).findById(any(Long.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.CANNOT_FOUND_GAMECHARACTER_SKILL.getMessage());
    }

    @DisplayName("캐릭터가 공격함")
    @Test
    public void GameCharacterAttack() {
        // given
//        Monster monster = buildMonster();
////        FightResponseDto fightResponseDto = new FightResponseDto(gameCharacter.getId(), monster.getId());
////        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
//        given(gameCharacterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(gameCharacter));
//        given(monsterRepository.findById(any(Long.class))).willReturn(java.util.Optional.ofNullable(monster));
//
//        // when
////        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
////        FightResponseDto afterFightResponseDto = gameCharacterService.gameCharacterAttack(fightResponseDto);
//
//        // then
//        then(gameCharacterRepository).should(times(2)).save(any(GameCharacter.class));
////        assertThat(gameCharacterResponseDto.getHp()).isEqualTo(gameCharacter.getHp());
    }
    
    @DisplayName("캐릭터가 공격받음")
    @Test
    public void GameCharacterUnderattack() {
        // given

        // when

        // then
    }

    @DisplayName("캐릭터 사망(HP <= 0)")
    @Test
    public void GameCharacterIsDead() {
        // given
//        GameCharacter gameCharacter = buildGameCharacter(1L, CharacterSpecies.HUMAN ,buildWeapon(CharacterSpecies.HUMAN));
//        gameCharacter.setHp(0F);
//        given(gameCharacterRepository.save(any(GameCharacter.class))).willReturn(gameCharacter);
//
//        // when
//        GameCharacter savedGameCharacter = gameCharacterService.addGameCharacter(gameCharacter);
//        assertThat(gameCharacterService.isDead(savedGameCharacter)).isTrue();
//        // then
//        then(gameCharacterRepository).should(times(1)).save(any(GameCharacter.class));
//        gameCharacterService.isDead(savedGameCharacter);
    }

    @DisplayName("캐릭터 레벨업")
    @Test
    public void GameCharacterLevelUp() {
        // given

        // when

        // then

    }


    private GameCharacter buildGameCharacter(Long id, CharacterSpecies characterSpecies, Weapon weapon) {
        return GameCharacter.builder()
                .id(id)
                .level(30)
                .hp(100F)
                .mp(100F)
                .attackPower(10F)
                .attackSpeed(30F)
                .defensePower(5F)
                .avoidanceRate(30F)
                .characterSpecies(characterSpecies)
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

    private Monster buildMonster() {
        return Monster.builder()
                .id(1L)
                .hp(100F)
                .attackPower(20F)
                .defensePower(15F)
                .counterattackRate(30F) // 반격 확률 Default = 30%
                .build();
    }

    private GameCharacterResponseDto buildGameCharacterResponseDto(GameCharacter savedGameCharacter, Skill skill, int skillExpiredDate) {
        return GameCharacterResponseDto.builder()
                .id(savedGameCharacter.getId())
                .level(savedGameCharacter.getLevel())
                .hp(savedGameCharacter.getHp())
                .mp(savedGameCharacter.getMp())
                .attackPower(savedGameCharacter.getAttackPower())
                .attackSpeed(savedGameCharacter.getAttackSpeed())
                .defensePower(savedGameCharacter.getDefensePower())
                .avoidanceRate(savedGameCharacter.getAvoidanceRate())
                .characterSpecies(savedGameCharacter.getCharacterSpecies())
                .weapon(savedGameCharacter.getWeapon())
                .skill(skill)     // 캐릭터가 현재 사용중인 스킬
                .skillExpiredDate(skillExpiredDate)   // 캐릭터가 현재 사용중인 스킬의 유효기간
                .build();
    }
}