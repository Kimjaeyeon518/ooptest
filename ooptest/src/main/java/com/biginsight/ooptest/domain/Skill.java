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
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CharacterSpecies characterSpecies;  // 해당 스킬 사용이 가능한 캐릭터 종족
    private String name;            // 스킬명
    private Integer requiredMp;     // 소요 mp
    private Integer requiredLevel;  // 필요 레벨
    private String effect;          // 스킬 효과
}
