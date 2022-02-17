package com.biginsight.ooptest;

import com.biginsight.ooptest.domain.*;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.GameCharacterRepository;
import com.biginsight.ooptest.repository.GameCharacterSkillRepository;
import com.biginsight.ooptest.service.*;
import com.biginsight.ooptest.serviceImpl.GameCharacterServiceImpl;
import com.biginsight.ooptest.serviceImpl.MonsterServiceImpl;
import com.biginsight.ooptest.serviceImpl.SkillServiceImpl;
import com.biginsight.ooptest.serviceImpl.WeaponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class OoptestApplicationTests {

	@Autowired
	private FightService fightService;
	@Autowired
	private GameCharacterService gameCharacterService;
	@Autowired
	private MonsterService monsterService;
	@Autowired
	private WeaponService weaponService;
	@Autowired
	private SkillService skillService;

	private Weapon weapon;
	private Skill skill;
	private Monster monster;

	// 싸움이 끝나면 캐릭터와 몬스터의 HP, MP 초기화
	@BeforeEach
	public void initTest() {
		GameCharacter initGameCharacter = gameCharacterService.findById(1L);
		initGameCharacter.setHp(200F);
		initGameCharacter.setMp(100F);
		gameCharacterService.save(initGameCharacter);

		Monster initMonster = monsterService.findById(1L);
		initMonster.setHp(300F);
		monsterService.save(initMonster);
	}

	@Test
	@DisplayName("실제 DB를 연동한 테스트를 위한 초기 데이터 INSERT")
	public void insertTestData() {
		weapon = buildWeapon(CharacterSpecies.HUMAN);
		skill = buildSkill(CharacterSpecies.HUMAN, 10F, 10);
		monster = buildMonster();
		Monster savedMonster = monsterService.save(monster);
		Skill savedSkill = skillService.save(skill);
		Weapon savedWeapon = weaponService.save(weapon);
		GameCharacter buildCharacter = buildGameCharacter(CharacterSpecies.HUMAN, savedWeapon);
		GameCharacter savedGameCharacter = gameCharacterService.save(buildCharacter);
	}

	@Test
	@DisplayName("캐릭터가 몬스터를 일방적으로 공격 (무기 착용, 스킬 사용 X)")
	public void fightSimulation() {
		try {
			for(int i=1; i< 30; i++) {
				GameCharacter foundGameCharacter = gameCharacterService.findById(1L);
				Monster foundMonster = monsterService.findById(1L);

				GameCharacterResponseDto gameCharacterResponseDto = buildGameCharacterResponseDto(foundGameCharacter, null);
				MonsterResponseDto monsterResponseDto = buildMonsterResponseDto(foundMonster);
				FightResponseDto fightResponseDto = new FightResponseDto(foundGameCharacter, foundMonster, gameCharacterResponseDto, monsterResponseDto);


				System.out.println("===================================================");
				System.out.println(i + "회차 공격");
				System.out.println("===================================================");
				FightResponseDto afterFightResponseDto = fightService.gameCharacterDoAttack(fightResponseDto);
				if(afterFightResponseDto == null) {
					System.out.println("몬스터가 죽었습니다.");
					break;
				}
			}
		} catch (ApiException | InterruptedException e) {
			if(e.getMessage().equals(ApiErrorCode.MONSTER_IS_DEAD)) {
				System.out.println("MONSTER IS DEAD !");
			}
			else if(e.getMessage().equals(ApiErrorCode.GAMECHARACTER_IS_DEAD)) {
				System.out.println("GAME CHARACTER IS DEAD !");
			}
		}
	}

	@Test
	@DisplayName("캐릭터가 몬스터를 일방적으로 공격 (무기 착용, 스킬 사용 O)")
	public void fightSimulation2() {

		// 캐릭터가 스킬 습득
		gameCharacterService.getSkill(1L, 1L);
		Skill foundSkill = skillService.findById(1L);
		// 캐릭터가 스킬 사용
		GameCharacterResponseDto useSkillGameCharacterResponseDto = gameCharacterService.useSkill(1L, 1L);

		try {
			for(int i=1; i< 50; i++) {
				GameCharacter foundGameCharacter = gameCharacterService.findById(1L);
				Monster foundMonster = monsterService.findById(1L);
				MonsterResponseDto monsterResponseDto = buildMonsterResponseDto(foundMonster);
				GameCharacterResponseDto gameCharacterResponseDto = buildGameCharacterResponseDto(foundGameCharacter
						, useSkillGameCharacterResponseDto.getSkill()
						, useSkillGameCharacterResponseDto.getSkillExpiredDate());

				FightResponseDto fightResponseDto = new FightResponseDto(foundGameCharacter, foundMonster, gameCharacterResponseDto, monsterResponseDto);

				System.out.println("===================================================");
				System.out.println(i + "회차 공격");
				System.out.println("===================================================");
				FightResponseDto afterFightResponseDto = fightService.gameCharacterDoAttack(fightResponseDto);
				if(afterFightResponseDto == null) {
					System.out.println("몬스터가 죽었습니다.");
					break;
				}
			}
		} catch (ApiException | InterruptedException e) {
			if(e.getMessage().equals(ApiErrorCode.MONSTER_IS_DEAD)) {
				System.out.println("MONSTER IS DEAD !");
			}
			else if(e.getMessage().equals(ApiErrorCode.GAMECHARACTER_IS_DEAD)) {
				System.out.println("GAME CHARACTER IS DEAD !");
			}
		}
	}

	@Test
	@DisplayName("몬스터가 캐릭터를 일방적으로 공격")
	public void fightSimulation3() {
		try {
			for(int i=1; i< 30; i++) {
				GameCharacter foundGameCharacter = gameCharacterService.findById(1L);
				Monster foundMonster = monsterService.findById(1L);

				GameCharacterResponseDto gameCharacterResponseDto = buildGameCharacterResponseDto(foundGameCharacter, null);
				MonsterResponseDto monsterResponseDto = buildMonsterResponseDto(foundMonster);
				FightResponseDto fightResponseDto = new FightResponseDto(foundGameCharacter, foundMonster, gameCharacterResponseDto, monsterResponseDto);


				System.out.println("===================================================");
				System.out.println(i + "회차 공격");
				System.out.println("===================================================");
				FightResponseDto afterFightResponseDto = fightService.monsterDoAttack(fightResponseDto);
				if(afterFightResponseDto == null) {
					System.out.println("캐릭터가 죽었습니다.");
					break;
				}
			}
		} catch (ApiException | InterruptedException e) {
			if(e.getMessage().equals(ApiErrorCode.MONSTER_IS_DEAD)) {
				System.out.println("MONSTER IS DEAD !");
			}
			else if(e.getMessage().equals(ApiErrorCode.GAMECHARACTER_IS_DEAD)) {
				System.out.println("GAME CHARACTER IS DEAD !");
			}
		}
	}

	private GameCharacter buildGameCharacter(CharacterSpecies characterSpecies, Weapon weapon) {
		return GameCharacter.builder()
				.level(30)
				.hp(100F)
				.mp(100F)
				.attackPower(10F)
				.attackSpeed(30)
				.defensePower(5F)
				.avoidanceRate(20F)
				.characterSpecies(characterSpecies)
				.gameCharacterSkillList(new ArrayList<>())
				.weapon(weapon)
				.build();
	}

	private Weapon buildWeapon(CharacterSpecies characterSpecies) {
		String name = "Short sword";
		String effect = "attackPower,+5%";

		return Weapon.builder()
				.characterSpecies(characterSpecies)
				.name(name)
				.effect(effect)
				.build();
	}

	private Skill buildSkill(CharacterSpecies characterSpecies, Float requiredMp, Integer requiredLevel) {
		return Skill.builder()
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
				.hp(100F)
				.attackPower(20F)
				.defensePower(15F)
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

	private GameCharacterResponseDto buildGameCharacterResponseDto(GameCharacter savedGameCharacter, Skill skill, Long skillExpiredDate) {

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
