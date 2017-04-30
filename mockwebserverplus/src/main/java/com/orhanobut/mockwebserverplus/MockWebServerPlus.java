package com.orhanobut.mockwebserverplus;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

/**
 * A wrapper for {@link MockWebServer} with more features
 */
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
    return mockWebServer.apply(base, description);
  }

  /**
   * Given paths will be parsed to fixtures and added to the queue. Can be multiple
   */
  public List<MockResponse> enqueue(String... paths) {
    if (paths == null) {
      return null;
    }
    List<MockResponse> mockResponseList = new ArrayList<>();
    for (String path : paths) {
      Fixture fixture = Fixture.parseFrom(path, parser);
      MockResponse mockResponse = fixture.toMockResponse();
      mockWebServer.enqueue(mockResponse);
      mockResponseList.add(mockResponse);
    }
    return mockResponseList;
  }

  /**
   * Given policy will be enqueued as MockResponse
   */
  public MockResponse enqueue(SocketPolicy socketPolicy) {
    MockResponse mockResponse = new MockResponse().setSocketPolicy(socketPolicy);
    mockWebServer.enqueue(mockResponse);
    return mockResponse;
  }

  /**
   * Given response will be enqueued
   */
  public void enqueue(MockResponse response) {
    mockWebServer.enqueue(response);
  }

  /**
   * Returns the url as endpoint
   *
   * @param path url path
   */
  public String url(String path) {
    return mockWebServer.url(path).toString();
  }

  /**
   * Returns the recorded request after execution
   */
  public RecordedRequest takeRequest() throws InterruptedException {
    return mockWebServer.takeRequest();
  }

  /**
   * A proxy method for MockWebServer dispatcher
   */
  public void setDispatcher(Dispatcher dispatcher) {
    mockWebServer.setDispatcher(dispatcher);
  }

  /**
   * Returns the wrapped MockWebServer instance.
   */
  public MockWebServer server() {
    return mockWebServer;
  }
}
