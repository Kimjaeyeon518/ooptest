package com.biginsight.ooptest.dto.response;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.Weapon;
import lombok.*;

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
    private Float attackSpeed; // 공격속도
    private Float defensePower; // 방어력
    private Float avoidanceRate; // 회피율 (%)
    private CharacterSpecies characterSpecies;     // 종족
    //    private List<Skill> skillList = new ArrayList();
    private Weapon weapon;

//    public void attack() {
//        if(this.weapon != null) {
//            String[] weaponEffects = weapon.getEffect().split(";");
//
//            for(String weaponEffect : weaponEffects) {
//                reflectFigure(weaponEffect);
//            }
//        }
//    }

    public GameCharacterResponseDto wearWeapon(Weapon weapon) {
        String weaponEffect = weapon.getEffect();
        System.out.println("weaponEffect = " + weaponEffect);
        return reflectFigure(weaponEffect);
    }

    public GameCharacterResponseDto reflectFigure(String effect) {
        String[] effectDetail = effect.split(",");    // 무기 효과를 받는 속성 (ex) attackPower, attackSpeed
        String effectDetailFigure = effectDetail[1].replaceAll("[^+^-^0-9]", "");   // 무기 효과 수치 (ex) +5%, -10 ...

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
