package com.droppa.DroppaUserService.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ErrorResponse(LocalDateTime timestamp,int status,String error,String message) {


}
