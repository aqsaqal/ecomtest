package com.example.ecomtest.api;

import com.example.ecomtest.aop.RateLimited;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


  /**
   * Test endpoint for request limiter.
   */
  @RateLimited
  @GetMapping("/test")
  public void test(HttpServletRequest request) {
  }
}

