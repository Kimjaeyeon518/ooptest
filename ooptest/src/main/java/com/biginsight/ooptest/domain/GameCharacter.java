package com.biginsight.ooptest.domain;

import com.biginsight.ooptest.dto.response.GameCharacterResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameCharacter {    // Character 로 사용 시 예약어 충돌 발생

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer level;
    private Float hp;
    private Float mp;
    private Float attackPower; // 공격력
    private Float attackSpeed; // 공격속도
    private Float defensePower; // 방어력
    private Float avoidanceRate; // 회피율 (%)

    @Enumerated(EnumType.STRING)
    private CharacterSpecies characterSpecies;     // 종족

    @OneToMany(mappedBy = "gameCharacter")
    private List<GameCharacterSkill> gameCharacterSkillList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "weapon_id", nullable = false)
    private Weapon weapon;

    public void attack() {
        if(this.weapon != null) {
            String[] weaponEffects = weapon.getEffect().split(";");

            for(String weaponEffect : weaponEffects) {
                String[] effectDetail = weaponEffect.split(",");    // 무기 효과를 받는 속성 (ex) attackPower, attackSpeed
                String effectDetailFigure = effectDetail[1].replaceAll("[^+^-^0-9]", "");   // 무기 효과 수치 (ex) +5%, -10 ...

                switch (effectDetail[0]) {
                    case "attackPower":
                        if(effectDetail[1].contains("%"))
                            this.attackPower *= (100 + Integer.valueOf(effectDetailFigure)) / 100;
                        else
                            this.attackPower += Integer.valueOf(effectDetailFigure);
                        break;

                    case "attackSpeed":
                        if(effectDetail[1].contains("%"))
                            this.attackSpeed *= (100 + Integer.valueOf(effectDetailFigure)) / 100;
                        else
                            this.attackSpeed += Integer.valueOf(effectDetailFigure);
                        break;

                    default:
                        break;
                }
            }
        }
    }
}
