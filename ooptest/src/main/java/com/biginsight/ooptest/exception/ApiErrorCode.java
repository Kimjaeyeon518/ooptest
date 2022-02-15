package com.biginsight.ooptest.exception;

import lombok.Getter;

@Getter
public enum ApiErrorCode {
    //COMMON
    INVALID_PARAMS(1000, "잘못된 요청입니다."),
    SERVER_TRANSACTION_ERROR(1001, "서버에서 요청을 처리하는 도중 오류가 발생하였습니다."),

    // GAMECHARACTER
    CANNOT_FOUND_GAMECHARACTER(2000, "아이디와 일치하는 캐릭터가 존재하지 않습니다."),
    INVALID_SPECIES(2001, "종족이 불일치합니다."),
    NOT_ENOUGH_MP(2002, "마나가 부족합니다."),
    NOT_ENOUGH_LEVEL(2003, "레벨이 낮아서 해당 스킬을 사용할 수 없습니다."),
    CANNOT_FOUND_SKILL(2004, "습득한 스킬이 아니라 사용할 수 없습니다."),

    // WEAPON
    CANNOT_FOUND_WEAPON(2000, "아이디와 일치하는 무기가 존재하지 않습니다."),

    TEMP(0, "TEMP");


    private final int statusCode;
    private final String message;

    ApiErrorCode(final int statusCode, final String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return this.message;
    }
}