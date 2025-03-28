package com.example.securitywithredis.global.exception;

import com.example.securitywithredis.global.common.api.BaseCode;
import com.example.securitywithredis.global.common.api.ResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseCode code;

    public ResponseDTO getErrorReason() {
        return this.code.getReason();
    }

    public ResponseDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}