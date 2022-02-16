package com.biginsight.ooptest.dto.response;

import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Monster;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonsterResponseDto {

    private Long id;
    private Float hp;
    private Float attackPower; // 공격력
    private Float defensePower; // 방어력
    private Float counterattackRate;

    public Monster toEntity() {
        return Monster.builder()
                .id(this.id)
                .hp(this.hp)
                .attackPower(this.attackPower)
                .defensePower(this.defensePower)
                .counterattackRate(this.counterattackRate)
                .build();
    }
}
