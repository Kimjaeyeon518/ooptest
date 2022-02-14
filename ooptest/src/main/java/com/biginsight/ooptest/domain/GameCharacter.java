package com.biginsight.ooptest.domain;

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

//    @OneToMany
//    private List<Skill> skillList = new ArrayList();
//
    @ManyToOne
    @JoinColumn(name = "weapon_id")
    private Weapon weapon;

    public void wearWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
}
