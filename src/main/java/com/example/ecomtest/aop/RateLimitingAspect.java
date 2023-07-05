package com.example.ecomtest.aop;

import com.example.ecomtest.service.RequestLimiter;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitingAspect {
  private RequestLimiter requestLimiter;

  @Autowired
  public RateLimitingAspect(RequestLimiter requestLimiter) {
    this.requestLimiter = requestLimiter;
  }

  @Around("@annotation(com.example.ecomtest.aop.RateLimited) && args(request,..)")
  public Object rateLimit(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
    if (!requestLimiter.isAllowed(request)) {
      throw new RateLimitException();
    }

    return joinPoint.proceed();
  }
}

