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
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer hp;
    private Integer attackPower; // 공격력
    private Integer defensePower; // 방어력
    private Integer counterattackRate; // 반격 (%) -> 캐릭터의 '회피율' 과 같은 개념으로 생각하고 일단 Skill 로 가져가지 않기로 함.

}
