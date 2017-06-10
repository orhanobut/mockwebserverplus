package com.orhanobut.mockwebserverplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;

/**
 * A value container that holds all information about the fixture file.
 */
public class Fixture {

  public int statusCode;
  public String body;
  public List<String> headers;
  public int delay;

  /**
   * Parse the given filename and returns the Fixture object.
   *
   * @param fileName filename should not contain extension or relative path. ie: login
   */
  public static Fixture parseFrom(String fileName) {
    return parseFrom(fileName, new YamlParser());
  }


  /**
   * Parse the given filename and returns the Fixture object.
   *
   * @param fileName filename should not contain extension or relative path. ie: login
   * @param parser   parser is required for parsing operation, it should not be null
   */
  public static Fixture parseFrom(String fileName, Parser parser) {
    if (fileName == null) {
      throw new NullPointerException("File name should not be null");
    }
    String path = "fixtures/" + fileName + ".yaml";
    InputStream inputStream = openPathAsStream(path);
    Fixture result = parser.parse(inputStream);

    if (result.body != null && !result.body.startsWith("{")) {
      String bodyPath = "fixtures/" + result.body;
      try {
        result.body = readPathIntoString(bodyPath);
      } catch (IOException e) {
        throw new IllegalStateException("Error reading body: " + bodyPath, e);
      }
    }
    return result;
  }

  private static InputStream openPathAsStream(String path) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = loader.getResourceAsStream(path);

    if (inputStream == null) {
      throw new IllegalStateException("Invalid path: " + path);
    }

    return inputStream;
  }

  private static String readPathIntoString(String path) throws IOException {
    InputStream inputStream = openPathAsStream(path);
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder out = new StringBuilder();
    int read;
    while ((read = reader.read()) != -1) {
      out.append((char) read);
    }
    reader.close();

    return out.toString();
  }

  public MockResponse toMockResponse() {
    MockResponse mockResponse = new MockResponse();
    if (this.statusCode != 0) {
      mockResponse.setResponseCode(this.statusCode);
    }
    if (this.body != null) {
      mockResponse.setBody(this.body);
    }
    if (this.delay != 0) {
      mockResponse.setBodyDelay(this.delay, TimeUnit.MILLISECONDS);
    }
    if (this.headers != null) {
      for (String header : this.headers) {
        mockResponse.addHeader(header);
      }
    }
    return mockResponse;
  }
}