package com.biginsight.ooptest;

import com.biginsight.ooptest.domain.*;
import com.biginsight.ooptest.dto.response.FightResponseDto;
import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import com.biginsight.ooptest.dto.response.MonsterResponseDto;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.*;
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
import java.util.List;

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

	@Autowired
	private WeaponRepository weaponRepository;
	@Autowired
	private SkillRepository skillRepository;
	@Autowired
	private GameCharacterRepository gameCharacterRepository;
	@Autowired
	private MonsterRepository monsterRepository;
	private Weapon weapon;
	private Skill skill;
	private Monster monster;

	// 싸움이 끝나면 캐릭터와 몬스터의 HP, MP 초기화
	@BeforeEach
	public void initTest() {
//		GameCharacter initGameCharacter = gameCharacterService.findById(1L);
//		initGameCharacter.setHp(200F);
//		initGameCharacter.setMp(100F);
//		gameCharacterService.save(initGameCharacter);
//
//		Monster initMonster = monsterService.findById(1L);
//		initMonster.setHp(300F);
//		monsterService.save(initMonster);
	}

	@Test
	@DisplayName("실제 DB를 연동한 테스트를 위한 초기 데이터 INSERT")
	public void insertTestData() {
		
		// 무기 데이터 저장
		List<Weapon> weaponList = new ArrayList<>();
		weaponList.add(buildDefaultWeapon());	// default 무기 (맨 손) - 아무 효과 없음
		weaponList.add(buildWeapon(CharacterSpecies.HUMAN, "Short sword", "attackPower,+5%"));	// 휴먼(검)
		weaponList.add(buildWeapon(CharacterSpecies.HUMAN, "Long sword", "attackPower,+10%"));	// 휴먼(검)
		weaponList.add(buildWeapon(CharacterSpecies.ELF, "Short bow", "attackSpeed,+5%"));	// 엘프(활)
		weaponList.add(buildWeapon(CharacterSpecies.ELF, "Long bow", "attackSpeed,+10%"));	// 엘프(활)
		weaponList.add(buildWeapon(CharacterSpecies.OAK, "Short Axe", "attackPower,+10%;attackSpeed,-5%"));	// 오크(둔기, 도끼)
		weaponList.add(buildWeapon(CharacterSpecies.OAK, "Iron Hammer", "attackPower,+20%;attackSpeed,-10%"));	// 오크(둔기, 도끼)
		weaponRepository.saveAll(weaponList);

		// 스킬 데이터 저장
		List<Skill> skillList = new ArrayList<>();
		// 일반 스킬
		skillList.add(buildSkill(CharacterSpecies.COMMON, "Heal", 20F, 1, "hp,+20", 10000L));	// 공통 스킬 (HP 가 오른다)
		skillList.add(buildSkill(CharacterSpecies.COMMON, "Steam", 30F, 1, "attackPower,+20%", 10L));	// 공통 스킬 (공격력 20% 상승)
		skillList.add(buildSkill(CharacterSpecies.HUMAN, "Guard", 40F, 1, "defensePower,+30%", 10L));	// 휴먼 스킬 (방어력 30% 상승)
		skillList.add(buildSkill(CharacterSpecies.ELF, "Elusion", 40F, 1, "avoidRate,+30%", 10L));	// 엘프 스킬 (회피력 30% 상승)
		skillList.add(buildSkill(CharacterSpecies.OAK, "Anger", 40F, 1, "attackPower,+50%;defensePower,-10%", 10L));	// 오크 스킬 (공격력 50% 상승, 방어력 10% 하락)
		// 궁극 스킬
		skillList.add(buildSkill(CharacterSpecies.HUMAN, "Invincible", 50F, 99, "defensePower,+1000000", 10L));	// 휴먼 궁극 스킬  (10초 동안 무적이 됨.)
		skillList.add(buildSkill(CharacterSpecies.ELF, "Rapid", 50F, 99, "attackSpeed,+500%", 60L));	// 엘프 궁극 스킬 (1분 동안 공격 속도가 500% 상승 한다.)
		skillList.add(buildSkill(CharacterSpecies.OAK, "Frenzy", 50F, 99, "attackPower,+500%", 60L));	// 오크 궁극 스킬  (1분 동안 공격력이 500% 상승 한다.)
		skillRepository.saveAll(skillList);


		// 몬스터 데이터 저장
		List<Monster> gameMonsterList = new ArrayList<>();
		gameMonsterList.add(buildMonster(300F, 20F, 10F));
		gameMonsterList.add(buildMonster(500F, 30F, 20F));
		gameMonsterList.add(buildMonster(1000F, 50F, 40F));
		monsterRepository.saveAll(gameMonsterList);


		// 캐릭터 데이터 저장 ( 모두 초기 능력치는 동일하고, 무기도 다같이 맨손으로 시작 )
		Weapon defaultWeapon = weaponService.findById(1L);	// 맨손 무기
		List<GameCharacter> gameCharacterList = new ArrayList<>();
		gameCharacterList.add(buildGameCharacter(CharacterSpecies.HUMAN, defaultWeapon));
		gameCharacterList.add(buildGameCharacter(CharacterSpecies.ELF, defaultWeapon));
		gameCharacterList.add(buildGameCharacter(CharacterSpecies.OAK, defaultWeapon));
		gameCharacterRepository.saveAll(gameCharacterList);
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
				.level(1)
				.hp(200F)
				.mp(100F)
				.attackPower(40F)
				.attackSpeed(50)
				.defensePower(10F)
				.avoidanceRate(20F)
				.characterSpecies(characterSpecies)
				.gameCharacterSkillList(new ArrayList<>())
				.weapon(weapon)
				.build();
	}

	private Weapon buildWeapon(CharacterSpecies characterSpecies, String name, String effect) {

		return Weapon.builder()
				.characterSpecies(characterSpecies)
				.name(name)
				.effect(effect)
				.build();
	}

	private Skill buildSkill(CharacterSpecies characterSpecies, String name, Float requiredMp, Integer requiredLevel, String effect, Long duration) {
		return Skill.builder()
				.characterSpecies(characterSpecies)
				.name(name)
				.requiredMp(requiredMp)
				.requiredLevel(requiredLevel)
				.gameCharacterSkillList(new ArrayList<>())
				.effect(effect)
				.duration(duration)
				.build();
	}

	private Weapon buildDefaultWeapon() {
		return Weapon.builder()
				.characterSpecies(CharacterSpecies.COMMON)
				.name("맨 손")
				.effect("")
				.build();
	}
	private Monster buildMonster(Float hp, Float attackPower, Float defensePower) {
		return Monster.builder()
				.hp(hp)
				.attackPower(attackPower)
				.defensePower(defensePower)
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
