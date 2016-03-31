package com.orhanobut.mockwebserverplus;

import java.io.InputStream;
import java.util.List;

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

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = loader.getResourceAsStream(path);

    if (inputStream == null) {
      throw new IllegalStateException("Invalid path: " + fileName);
    }
    return parser.parse(inputStream);
  }
}
