package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.*;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.MonsterRepository;
import com.biginsight.ooptest.service.GameCharacterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;

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

    @Mock
    private GameCharacterService gameCharacterService;

    private GameCharacter gameCharacter;
    private Weapon weapon;
    private Skill skill;
    private Monster monster;

    @BeforeEach
    public void initTest() {
        gameCharacter = buildGameCharacter(1L, CharacterSpecies.HUMAN, buildDefaultWeapon());
        weapon = buildWeapon(CharacterSpecies.HUMAN);
        skill = buildSkill(CharacterSpecies.HUMAN, 10F, 10);
        monster = buildMonster();
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

    @DisplayName("몬스터가 캐릭터를 공격")
    @Test
    public void MonsterAttack() {
        // given
        GameCharacterResponseDto originalGameCharacterResponseDto = buildGameCharacterResponseDto(gameCharacter, null);
        MonsterResponseDto originalMonsterResponseDto = buildMonsterResponseDto(monster);
        GameCharacterResponseDto reflectedGameCharacterResponseDto = buildGameCharacterResponseDto(gameCharacter, null);
        MonsterResponseDto reflectedMonsterResponseDto = buildMonsterResponseDto(monster);
        reflectedGameCharacterResponseDto.setHp(50F);
        FightResponseDto fightResponseDto = new FightResponseDto(originalGameCharacterResponseDto, originalMonsterResponseDto, reflectedGameCharacterResponseDto, reflectedMonsterResponseDto);
        given(gameCharacterService.underattack(any(FightResponseDto.class))).willReturn(fightResponseDto);

        // when
        FightResponseDto afterFightResponseDto = monsterService.doAttack(fightResponseDto);

        // then
        then(gameCharacterService).should(times(1)).underattack(any(FightResponseDto.class));
        assertThat(afterFightResponseDto.getReflectedGameCharacterResponseDto().getHp()).isEqualTo(afterFightResponseDto.getOriginalGameCharacterResponseDto().getHp() - 50F);
    }

    @DisplayName("몬스터가 공격받음")
    @Test
    public void MonsterUnderattack() {
        // given
        monster.setCounterattackRate(0F);
        GameCharacterResponseDto originalGameCharacterResponseDto = buildGameCharacterResponseDto(gameCharacter, null);
        MonsterResponseDto originalMonsterResponseDto = buildMonsterResponseDto(monster);
        GameCharacterResponseDto reflectedGameCharacterResponseDto = buildGameCharacterResponseDto(gameCharacter, null);
        MonsterResponseDto reflectedMonsterResponseDto = buildMonsterResponseDto(monster);
        FightResponseDto fightResponseDto = new FightResponseDto(originalGameCharacterResponseDto, originalMonsterResponseDto, reflectedGameCharacterResponseDto, reflectedMonsterResponseDto);
        given(monsterRepository.save(any(Monster.class))).willReturn(monster);

        // when
        FightResponseDto afterFightResponseDto = monsterService.underattack(fightResponseDto);

        // then
        then(monsterRepository).should(times(1)).save(any(Monster.class));
        assertThat(afterFightResponseDto.getReflectedMonsterResponseDto().getHp()).isEqualTo(afterFightResponseDto.getOriginalMonsterResponseDto().getHp() - 5F);
    }

    @DisplayName("몬스터가 공격받다가 사망(HP <= 0)")
    @Test
    public void MonsterIsDead() {
        // given
        monster.setCounterattackRate(0F);
        gameCharacter.setAttackPower(1000F);  // 캐릭터 공격력 1000
        GameCharacterResponseDto originalGameCharacterResponseDto = buildGameCharacterResponseDto(gameCharacter, null);
        MonsterResponseDto originalMonsterResponseDto = buildMonsterResponseDto(monster);
        GameCharacterResponseDto reflectedGameCharacterResponseDto = buildGameCharacterResponseDto(gameCharacter, null);
        MonsterResponseDto reflectedMonsterResponseDto = buildMonsterResponseDto(monster);
        FightResponseDto fightResponseDto = new FightResponseDto(originalGameCharacterResponseDto, originalMonsterResponseDto, reflectedGameCharacterResponseDto, reflectedMonsterResponseDto);
        given(monsterRepository.save(any(Monster.class))).willReturn(monster);

        // when
        ApiException exception = assertThrows(ApiException.class,
                () -> monsterService.underattack(fightResponseDto));

        // then
        then(gameCharacterService).should(times(1)).levelUp(any(GameCharacter.class));
        then(monsterRepository).should(times(1)).save(any(Monster.class));
        assertThat(exception.getMessage()).isEqualTo(ApiErrorCode.MONSTER_IS_DEAD.getMessage());
    }

    @DisplayName("몬스터가 반격")
    @Test
    public void MonsterCounterAttack() {
        // given
        monster.setCounterattackRate(100F); // 반격율 100%
        gameCharacter.setAvoidanceRate(100F);
        GameCharacterResponseDto originalGameCharacterResponseDto = buildGameCharacterResponseDto(gameCharacter, null);
        MonsterResponseDto originalMonsterResponseDto = buildMonsterResponseDto(monster);
        GameCharacterResponseDto reflectedGameCharacterResponseDto = buildGameCharacterResponseDto(gameCharacter, null);
        MonsterResponseDto reflectedMonsterResponseDto = buildMonsterResponseDto(monster);
        FightResponseDto fightResponseDto = new FightResponseDto(originalGameCharacterResponseDto, originalMonsterResponseDto, reflectedGameCharacterResponseDto, reflectedMonsterResponseDto);


        // when
        FightResponseDto afterFightResponseDto = monsterService.underattack(fightResponseDto);

        // then
        then(monsterRepository).should(times(1)).save(any(Monster.class));
        assertThat(afterFightResponseDto.getReflectedMonsterResponseDto().getHp()).isEqualTo(afterFightResponseDto.getOriginalMonsterResponseDto().getHp() - 5F);
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
                .avoidanceRate(0F)
                .characterSpecies(characterSpecies)
                .gameCharacterSkillList(new ArrayList<>())
                .weapon(weapon)
                .build();
    }

    private Weapon buildDefaultWeapon() {
        return Weapon.builder()
                .id(1L)
                .characterSpecies(CharacterSpecies.COMMON)
                .name("default_weapon")
                .effect("attackPower,+5")
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

    private Monster buildMonster() {
        return Monster.builder()
                .id(1L)
                .hp(100F)
                .attackPower(20F)
                .defensePower(5F)
                .counterattackRate(30F) // 반격 확률 Default = 30%
                .build();
    }

    private MonsterResponseDto buildMonsterResponseDto(Monster monster) {
        return MonsterResponseDto.builder()
                .id(monster.getId())
                .hp(monster.getHp())
                .attackPower(monster.getAttackPower())
                .defensePower(monster.getDefensePower())
                .counterattackRate(monster.getCounterattackRate()) // 반격 확률 Default = 30%
                .build();
    }

    private GameCharacterResponseDto buildGameCharacterResponseDto(GameCharacter savedGameCharacter, Skill skill) {
        long skillExpiredDate = 0;

        if(skill != null) {
            Date date = new Date();
            skillExpiredDate = date.getTime() / 1000L + skill.getDuration();     // 밀리세컨까지는 필요없으므로 1000으로 나눔 + 스킬의 지속시간(초)
        }

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