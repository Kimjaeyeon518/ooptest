package com.biginsight.ooptest.dto.response;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FightResponseDto {
    // 무기나 스킬을 장착, 사용 중임을 표시하고 실제로 효과를 적용시키지는 않은 DTO
    private GameCharacter gameCharacter;
    private Monster monster;

    // 실제 무기 / 스킬 효과를 적용하면서 싸울 DTO
    private GameCharacterResponseDto gameCharacterResponseDto;
    private MonsterResponseDto monsterResponseDto;
}
