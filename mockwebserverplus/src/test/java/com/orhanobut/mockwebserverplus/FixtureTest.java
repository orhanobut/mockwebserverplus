package com.orhanobut.mockwebserverplus;

import org.junit.Test;

import fixtures.Fixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class FixtureTest {

  final Parser parser = new YamlParser();

  @Test public void parseWithoutParser() {
    Fixture fixture = Fixture.parseFrom(Fixtures.SIMPLE_WITH_DELAY);

    assertThat(fixture.statusCode).isEqualTo(200);
    assertThat(fixture.delay).isEqualTo(300);
    assertThat(fixture.body).isEqualTo("{result:{}}");
    assertThat(fixture.headers).containsExactly("Auth:auth", "key:value");
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
      assertThat(e).hasMessage("Invalid path: fixtures/invalid_path.yaml");
    }

    try {
      Fixture.parseFrom(null, parser);
      fail("should fail");
    } catch (Exception e) {
      assertThat(e).hasMessage("File name should not be null");
    }
  }

  @Test public void parseFixtureWithBodyReference() {
    Fixture fixture = Fixture.parseFrom(Fixtures.SIMPLE_BODY_FILE, parser);

    assertThat(fixture.statusCode).isEqualTo(200);
    assertThat(fixture.delay).isEqualTo(0);
    assertThat(fixture.body).isEqualTo("{\n" +
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
        "}");
    assertThat(fixture.headers).containsExactly("Auth:auth", "key:value");
  }
}