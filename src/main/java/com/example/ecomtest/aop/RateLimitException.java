package com.example.ecomtest.aop;

public class RateLimitException extends RuntimeException {
  public RateLimitException() {
    super("Rate limit exceeded.");
  }
}

