package com.orhanobut.mockwebserverplus;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import fixtures.Fixtures;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.QueueDispatcher;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

import static com.orhanobut.mockwebserverplus.MockWebServerPlusTest.AssertFixture.assertFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class MockWebServerPlusTest {

  private static final String JSON_RESPONSE = "{\n" +
      "  \"array\": [\n" +
      "    1,\n" +
      "    2,\n" +
      "    3\n" +
      "  ],\n" +
      "  \"boolean\": true,\n" +
      "  \"null\": null,\n" +
      "  \"number\": 123,\n" +
      "  \"object\": {\n" +
      "    \"a\": \"b\",\n" +
      "    \"c\": \"d\",\n" +
      "    \"e\": \"f\"\n" +
      "  },\n" +
      "  \"string\": \"Hello World\"\n" +
      "}";

  @Rule public MockWebServer mockWebServer = new MockWebServer();

  final MockWebServerPlus server = new MockWebServerPlus(mockWebServer);
  final OkHttpClient httpClient = new OkHttpClient();

  @Test public void useDefaultMockWebServer() {
    assertThat(new MockWebServerPlus().server()).isNotNull();
  }

  @Test public void generateValidUrl() {
    assertThat(server.url("")).isNotNull();
  }

  @Test public void enqueueSingleResponse() throws Exception {
    server.enqueue(Fixtures.SIMPLE);

    Response response = execute();

    assertFixture(response)
        .body(JSON_RESPONSE)
        .statusCode(200)
        .header("Auth", "auth")
        .header("key", "value");
  }

  @Test public void enqueueMultipleResponse() throws Exception {
    server.enqueue(Fixtures.SIMPLE, Fixtures.SIMPLE);

    Response response = execute();

    assertFixture(response)
        .body(JSON_RESPONSE)
        .statusCode(200)
        .header("Auth", "auth")
        .header("key", "value");

    Response response2 = execute();

    assertFixture(response2)
        .body(JSON_RESPONSE)
        .statusCode(200)
        .header("Auth", "auth")
        .header("key", "value");
  }

  @Test public void enqueueWithoutHeaders() throws Exception {
    server.enqueue(Fixtures.SIMPLE_NO_HEADERS);

    Response response = execute();

    assertFixture(response)
        .body(JSON_RESPONSE)
        .statusCode(200);
  }

  @Test public void enqueueWithoutBody() throws Exception {
    server.enqueue(Fixtures.SIMPLE_NO_BODY);

    Response response = execute();

    assertFixture(response).statusCode(200);
  }

  @Test public void enqueueWithoutStatusCode() throws Exception {
    server.enqueue(Fixtures.SIMPLE_NO_STATUS_CODE);

    Response response = execute();

    assertFixture(response)
        .body(JSON_RESPONSE)
        .statusCode(200);
  }

  @Test public void enqueueSocketPolicy() throws IOException {
    server.enqueue(SocketPolicy.KEEP_OPEN);
    QueueDispatcher dispatcher = new QueueDispatcher();
    server.setDispatcher(dispatcher);

    MockResponse mockResponse = dispatcher.peek();

    assertThat(mockResponse.getSocketPolicy()).isEqualTo(SocketPolicy.KEEP_OPEN);
  }

  @Test public void interceptDispatch() throws InterruptedException, IOException {
    Dispatcher dispatcher = spy(new QueueDispatcher());
    server.setDispatcher(dispatcher);
    server.enqueue(new MockResponse());

    execute();

    verify(dispatcher).dispatch(any(RecordedRequest.class));
  }

  @Test public void getRecordedRequest() throws IOException, InterruptedException {
    server.enqueue(new MockResponse());

    execute();

    RecordedRequest recordedRequest = server.takeRequest();

    assertThat(recordedRequest).isNotNull();
  }

  @Test public void getMockServerInstance() {
    assertThat(server.server()).isEqualTo(mockWebServer);
  }

  private Response execute() throws IOException {
    return execute(server.url("/"));
  }

  private Response execute(String url) throws IOException {
    Request request = new Request.Builder()
        .url(server.url(url))
        .get()
        .build();

    return httpClient.newCall(request).execute();
  }

  static class AssertFixture {
    private final Response response;

    private AssertFixture(Response response) {
      this.response = response;
    }

    static AssertFixture assertFixture(Response response) {
      return new AssertFixture(response);
    }

    AssertFixture body(String body) throws IOException {
      assertThat(response.body().string()).isEqualTo(body);
      return this;
    }

    AssertFixture statusCode(int statusCode) {
      assertThat(response.code()).isEqualTo(statusCode);
      return this;
    }

    AssertFixture header(String key, String value) {
      assertThat(response.header(key)).isEqualTo(value);
      return this;
    }
  }

}