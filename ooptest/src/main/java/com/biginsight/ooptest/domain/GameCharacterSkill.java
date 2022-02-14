package com.biginsight.ooptest.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
public class GameCharacterSkill {       // 캐릭터가 스킬을 습득했음을 나타내는 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)      // GameCharacterSkill 을 SELECT 하는 쿼리 발생을 방지하기 위해 FechType을 LAZY로 설정
    @JoinColumn(name = "gameCharacter_id")
    private GameCharacter gameCharacter;

    @ManyToOne(fetch = FetchType.LAZY)      // GameCharacterSkill 을 SELECT 하는 쿼리 발생을 방지하기 위해 FechType을 LAZY로 설정 
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Builder
    public GameCharacterSkill(Long id, GameCharacter gameCharacter, Skill skill) {
        this.id = id;
        this.gameCharacter = gameCharacter;
        this.skill = skill;
//        this.gameCharacter.getGameCharacterSkillList().add(this);  // 양방향 매핑
//        this.skill.getGameCharacterSkillList().add(this);    // 양방향 매핑
    }
}
