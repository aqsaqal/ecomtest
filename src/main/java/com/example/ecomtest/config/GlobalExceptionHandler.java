package com.example.ecomtest.config;

import com.example.ecomtest.aop.RateLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_GATEWAY) // 502
  @ExceptionHandler(RateLimitException.class)
  public void handleRateLimitException() {
    // Nothing to do
  }
}

