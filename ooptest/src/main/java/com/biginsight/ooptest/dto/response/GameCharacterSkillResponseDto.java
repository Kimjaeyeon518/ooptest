package com.biginsight.ooptest.dto.response;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.domain.Weapon;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameCharacterSkillResponseDto {

    private Long id;
    private GameCharacter gameCharacter;
    private Skill skill;
}
