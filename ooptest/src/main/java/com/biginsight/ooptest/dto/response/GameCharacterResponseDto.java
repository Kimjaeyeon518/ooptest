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
}
