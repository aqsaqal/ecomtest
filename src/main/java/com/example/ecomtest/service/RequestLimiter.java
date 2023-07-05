package com.example.ecomtest.service;

import com.example.ecomtest.aop.RateLimited;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limits number of requests from one IP address.
 */
@Component
public class RequestLimiter {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestLimiter.class);

  // key - client IP address, value - number of requests from this IP address
  private final Map<String, AtomicInteger> requestCountsPerIpAddress = new ConcurrentHashMap<>();
  // max number of requests from one IP address per minutesInterval minutes
  private final int maxRequestCount;
  // interval in minutes
  private final int minutesInterval;

  /**
   * Creates RequestLimiter instance.
   * @param maxRequestCount max number of requests from one IP address per minutesInterval minutes
   * @param minutesInterval interval in minutes
   */
  public RequestLimiter(@Value("${request.limiter.max-request-count}") int maxRequestCount,
      @Value("${request.limiter.minutes-interval}") int minutesInterval) {
    this.maxRequestCount = maxRequestCount;
    this.minutesInterval = minutesInterval;
  }

  /**
   * Clears requestCountsPerIpAddress map every minutesInterval minutes.
   * This is not the best solution, but it is simple and works.
   * A better solution would be to clear only entries that are older than minutesInterval minutes.
   * But this would require more complex data structure and more complex logic.
   */
  @PostConstruct
  public void scheduleClearMap() {
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    scheduledExecutorService.scheduleAtFixedRate(this::clearMap, minutesInterval, minutesInterval, TimeUnit.MINUTES);
    LOGGER.info("Scheduled clearing of requests map every {} minutes", minutesInterval);
  }

  public void clearMap() {
    requestCountsPerIpAddress.clear();
    LOGGER.info("Cleared requests map");
  }

  /**
   * Checks if the request is allowed.
   * @param request HTTP request
   * @return true if the request is allowed, false otherwise
   */
  public boolean isAllowed(HttpServletRequest request) {
    String clientIp = getClientIP(request);
    AtomicInteger count = requestCountsPerIpAddress.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
    LOGGER.info("Request count for {}: {}", clientIp, count.get() + 1);
    return count.incrementAndGet() <= maxRequestCount;
  }

  /**
   * Returns client IP address.
   * @param request HTTP request
   * @return client IP address
   */
  private String getClientIP(HttpServletRequest request) {
    // if there is proxy, then the client ip will be in the X-Forwarded-For header
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty()) {
      return request.getRemoteAddr();
    }
    // The X-Forwarded-For header can contain multiple IP addresses
    return xfHeader.split(",")[0];
  }

  // This is just an example of how to use the RequestLimiter class
  @RateLimited
  public void rateLimitedMethod(HttpServletRequest request) {
    // Your method logic here
  }
}

