package com.biginsight.ooptest.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float hp;
    private Float attackPower; // 공격력
    private Float defensePower; // 방어력
    private Float counterattackRate; // 반격 (%) -> 캐릭터의 '회피율' 과 같은 개념으로 생각하고 일단 Skill 로 가져가지 않기로 함.

}
