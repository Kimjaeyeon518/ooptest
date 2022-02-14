package com.biginsight.ooptest.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Weapon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CharacterSpecies characterSpecies;  // 해당 무기 사용이 가능한 캐릭터 종족

    private String name;    // 무기 이름
    private String effect;  // 무기 효과


}
