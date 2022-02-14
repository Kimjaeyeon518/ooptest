package com.biginsight.ooptest.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CharacterSpecies {

    COMMON("공통"),
    HUMAN("휴먼"),
    OAK("오크"),
    ELF("엘프");

    private String value;
}
