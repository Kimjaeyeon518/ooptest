package com.biginsight.ooptest.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {

    private boolean success;    // 응답 성공 여부: True/False
    private int code;       // 응답 코드: 커스텀 예외 처리(>= 0), 미처 처리하지 못한 예외 발생(< 0)
    private String msg;     // 응답 메시지
}
