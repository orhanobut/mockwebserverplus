package com.orhanobut.mockwebserverplus;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class MockWebServerPlus implements TestRule {

  private final MockWebServer mockWebServer;
  private final Parser parser = new YamlParser();

  public MockWebServerPlus() {
    this.mockWebServer = new MockWebServer();
  }

  public MockWebServerPlus(MockWebServer mockWebServer) {
    this.mockWebServer = mockWebServer;
  }

  @Override public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        mockWebServer.apply(base, description);
      }
    };
  }

  public void enqueue(String... paths) {
    if (paths == null) {
      return;
    }

    for (String path : paths) {
      Fixture fixture = Fixture.parseFrom(path, parser);
      MockResponse mockResponse = new MockResponse()
          .setResponseCode(fixture.statusCode)
          .setBody(fixture.body)
          .setBodyDelay(fixture.delay, TimeUnit.SECONDS);

      for (String header : fixture.headers) {
        mockResponse.addHeader(header);
      }

      mockWebServer.enqueue(mockResponse);
    }
  }

  public String url(String path) {
    return mockWebServer.url(path).toString();
  }

  public RecordedRequest takeRequest() throws InterruptedException {
    return mockWebServer.takeRequest();
  }

  public MockWebServer server() {
    return mockWebServer;
  }
}
