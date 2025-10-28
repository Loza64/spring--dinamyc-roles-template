package com.server.app.dto;

import lombok.Data;

@Data
public class ExceptionResponse {
    private int status;
    private String message;
}
