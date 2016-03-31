package com.orhanobut.mockwebserverplus;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.QueueDispatcher;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Delays {@link okhttp3.mockwebserver.MockWebServer} response for specified seconds.
 */
public class DelayedDispatcher extends QueueDispatcher {
  private final Logger logger = Logger.getLogger(DelayedDispatcher.class.getName());
  private final long delayMillis;

  public DelayedDispatcher(long delay, TimeUnit unit) {
    this.delayMillis = unit.toMillis(delay);
  }

  @Override public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    logger.info("delaying response for " + delayMillis + " millis");
    Thread.sleep(delayMillis);
    return super.dispatch(request);
  }
}
