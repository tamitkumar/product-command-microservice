package com.tech.brain.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ERR000( "ERR.000", "The Web service authentication is invalid."),
    ERR001( "ERR.001", "Bad Request."),
    ERR002( "ERR.002", "No Product Found for update."),
    ERR003( "ERR.003", "Date format error."),
    ERR004( "ERR.004", "Ineternal server error."),
    ERR005( "ERR.005", "Error Occurred while conversion of object to JSON."),
    ERR006( "ERR.006", "Error Occurred while parsing json."),
    ERR009( "ERR.009", "Product Already Exists."),
    ERR010( "ERR.010", "Database connection error."),
    HTTP_CODE_500( "500", "Error when processing the request.");

    private String errorCode;
    private String errorMessage;

    ErrorCode(String code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

}
