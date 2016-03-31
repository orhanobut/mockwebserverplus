package com.orhanobut.mockwebserverplus;

import org.junit.Before;
import org.junit.Test;

import fixtures.Fixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class FixtureTest {

  Parser parser;

  @Before public void setup() {
    parser = new YamlParser();
  }

  @Test public void parseFixtureFromYaml() {
    Fixture fixture = Fixture.parseFrom(Fixtures.SIMPLE_WITH_DELAY, parser);

    assertThat(fixture.statusCode).isEqualTo(200);
    assertThat(fixture.delay).isEqualTo(300);
    assertThat(fixture.body).isEqualTo("{result:{}}");
    assertThat(fixture.headers).containsExactly("Auth:auth", "key:value");
  }

  @Test public void invalidPathThrowsExceptionOnParse() {
    try {
      Fixture.parseFrom("invalid_path", parser);
      fail("should fail");
    } catch (Exception e) {
      assertThat(e).hasMessage("Invalid path: invalid_path");
    }

    try {
      Fixture.parseFrom(null, parser);
      fail("should fail");
    } catch (Exception e) {
      assertThat(e).hasMessage("File name should not be null");
    }
  }
}