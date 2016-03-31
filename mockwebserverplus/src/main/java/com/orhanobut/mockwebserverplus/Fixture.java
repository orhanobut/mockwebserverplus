package com.orhanobut.mockwebserverplus;

import java.io.InputStream;
import java.util.List;

class Fixture {

  public int statusCode;
  public String body;
  public List<String> headers;
  public int delay;

  public static Fixture parseFrom(String fileName, Parser parser) {
    if (fileName == null) {
      throw new NullPointerException("File name should not be null");
    }
    String path = "fixtures/" + fileName + ".yaml";

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = loader.getResourceAsStream(path);

    if (inputStream == null) {
      throw new IllegalStateException("Invalid path: " + fileName);
    }
    return parser.parse(inputStream);
  }
}
