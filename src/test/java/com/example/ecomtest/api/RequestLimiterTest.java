package com.example.ecomtest.api;

import com.example.ecomtest.service.RequestLimiter;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestLimiterTest {

  private static final String TEST_IP = "192.168.0.1";
  private static final int MAX_REQUEST_COUNT = 5;
  private static final int MINUTES_INTERVAL = 1;

  @Test
  public void shouldLimitRequests() {
    // create mock request
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRemoteAddr()).thenReturn(TEST_IP);

    // create RequestLimiter instance
    RequestLimiter requestLimiter = new RequestLimiter(MAX_REQUEST_COUNT, MINUTES_INTERVAL);

    // make 5 allowed requests
    for (int i = 0; i < MAX_REQUEST_COUNT; i++) {
      assertTrue(requestLimiter.isAllowed(request));
    }

    // the 6th request should be denied
    assertFalse(requestLimiter.isAllowed(request));
  }

  @Test
  public void shouldAllowRequestsAfterInterval() throws InterruptedException {
    // create mock request
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRemoteAddr()).thenReturn(TEST_IP);

    // create RequestLimiter instance
    RequestLimiter requestLimiter = new RequestLimiter(MAX_REQUEST_COUNT, MINUTES_INTERVAL);

    // make 5 allowed requests
    for (int i = 0; i < MAX_REQUEST_COUNT; i++) {
      assertTrue(requestLimiter.isAllowed(request));
    }

    // sleep for MINUTES_INTERVAL minutes
    TimeUnit.MINUTES.sleep(MINUTES_INTERVAL);
    requestLimiter.clearMap();

    // the 6th request should be allowed
    assertTrue(requestLimiter.isAllowed(request));
  }
}


