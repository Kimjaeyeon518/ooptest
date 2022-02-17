package com.biginsight.ooptest.dto.response;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.domain.Weapon;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameCharacterResponseDto {

    private Long id;
    private Integer level;
    private Float hp;
    private Float mp;
    private Float attackPower; // 공격력
    private Integer attackSpeed; // 공격속도
    private Float defensePower; // 방어력
    private Float avoidanceRate; // 회피율 (%)
    private CharacterSpecies characterSpecies;     // 종족
    //    private List<Skill> skillList = new ArrayList();
    private Weapon weapon;
    private Skill skill;        // 현재 사용중인 스킬
    private long skillExpiredDate;   // 스킬 유효기간(초)

    public GameCharacter toEntity() {
        return GameCharacter.builder()
                .id(this.id)
                .level(this.level)
                .hp(this.hp)
                .mp(this.mp)
                .attackPower(this.attackPower)
                .attackSpeed(this.attackSpeed)
                .defensePower(this.defensePower)
                .characterSpecies(this.characterSpecies)
                .avoidanceRate(this.avoidanceRate)
                .weapon(this.weapon)
                .build();
    }

    public GameCharacterResponseDto reflectWeapon(Weapon weapon) {
        String weaponEffect = weapon.getEffect();
        return reflectFigure(weaponEffect);
    }

    public GameCharacterResponseDto reflectSkill(Skill skill) {
        String skillEffect = skill.getEffect();
        return reflectFigure(skillEffect);
    }

    public GameCharacterResponseDto reflectFigure(String effect) {
        String[] effectDetail = effect.split(",");    // 효과를 받는 속성 (ex) attackPower, attackSpeed
        String effectDetailFigure = effectDetail[1].replaceAll("[^+^-^0-9]", "");   // 효과 수치 (ex) +5%, -10 ...

        if(effectDetail[1].contains("%")) {
            switch (effectDetail[0]) {
                case "attackPower":
                    this.setAttackPower(this.getAttackPower() * (100 + Integer.valueOf(effectDetailFigure)) / 100);
                    break;

                case "attackSpeed":
                    this.setAttackSpeed(this.getAttackSpeed() * (100 + Integer.valueOf(effectDetailFigure)) / 100);
                    break;

                case "hp":
                    this.setHp(this.getHp() * (100 + Integer.valueOf(effectDetailFigure)) / 100);
                    break;

                case "mp":
                    this.setMp(this.getMp() * (100 + Integer.valueOf(effectDetailFigure)) / 100);
                    break;

                case "defensePower":
                    this.setDefensePower(this.getDefensePower() * (100 + Integer.valueOf(effectDetailFigure)) / 100);
                    break;

                case "avoidanceRate":
                    this.setAvoidanceRate(this.getAvoidanceRate() * (100 + Integer.valueOf(effectDetailFigure)) / 100);
                    break;

                default:
                    break;
            }
        }
        else {
            switch (effectDetail[0]) {
                case "attackPower":
                    this.setAttackPower(this.getAttackPower() + Integer.valueOf(effectDetailFigure));
                    break;

                case "attackSpeed":
                    this.setAttackSpeed(this.getAttackSpeed() + Integer.valueOf(effectDetailFigure));
                    break;

                case "hp":
                    this.setHp(this.getHp() + Integer.valueOf(effectDetailFigure));
                    break;

                case "mp":
                    this.setMp(this.getMp() + Integer.valueOf(effectDetailFigure));
                    break;

                case "defensePower":
                    this.setDefensePower(this.getDefensePower() + Integer.valueOf(effectDetailFigure));
                    break;

                case "avoidanceRate":
                    this.setAvoidanceRate(this.getAvoidanceRate() + Integer.valueOf(effectDetailFigure));
                    break;

                default:
                    break;
            }
        }
        return this;
    }
}
