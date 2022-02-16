package com.biginsight.ooptest.dto.response;

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
}
