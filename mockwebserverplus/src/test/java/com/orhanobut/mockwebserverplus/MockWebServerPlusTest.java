package com.orhanobut.mockwebserverplus;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import fixtures.Fixtures;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockWebServer;

import static org.assertj.core.api.Assertions.assertThat;

public class MockWebServerPlusTest {

  @Rule public MockWebServer mockWebServer = new MockWebServer();

  MockWebServerPlus server;
  OkHttpClient httpClient;

  @Before public void setup() throws IOException {
    server = new MockWebServerPlus(mockWebServer);
    httpClient = new OkHttpClient();
  }

  @Test public void testUrl() {
    assertThat(server.url("")).isNotNull();
  }

  @Test public void testEnqueueSingleResponse() throws Exception {
    server.enqueue(Fixtures.SIMPLE);

    Request request = new Request.Builder()
        .url(server.url("/"))
        .get()
        .build();

    Response response = httpClient.newCall(request).execute();

    new AssertFixture(response)
        .body("{result:{}}")
        .statusCode(200)
        .header("Auth", "auth")
        .header("key", "value");
  }

  @Test public void testEnqueueMultipleResponse() throws Exception {
    server.enqueue(Fixtures.SIMPLE, Fixtures.SIMPLE);

    Request request = new Request.Builder()
        .url(server.url("/"))
        .get()
        .build();

    Response response = httpClient.newCall(request).execute();

    new AssertFixture(response)
        .body("{result:{}}")
        .statusCode(200)
        .header("Auth", "auth")
        .header("key", "value");

    Response response2 = httpClient.newCall(request).execute();

    new AssertFixture(response2)
        .body("{result:{}}")
        .statusCode(200)
        .header("Auth", "auth")
        .header("key", "value");
  }

  private static class AssertFixture {
    private final Response response;

    public AssertFixture(Response response) {
      this.response = response;
    }

    public AssertFixture body(String body) throws IOException {
      assertThat(response.body().string()).isEqualTo(body);
      return this;
    }

    public AssertFixture statusCode(int statusCode) {
      assertThat(response.code()).isEqualTo(statusCode);
      return this;
    }

    public AssertFixture header(String key, String value) {
      assertThat(response.header(key)).isEqualTo(value);
      return this;
    }
  }

}