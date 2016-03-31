### mockwebserver PLUS

#### Problem
MockWebServer is a great tool for mocking network requests/responses.
In order to add response, you need to set mockresponse body with all
properties you need

```java
@Rule public MockWebServer server = new MockWebServer();

@Test public void uglyTest() {
  server.enqueue(new MockResponse()
      .setStatusCode(200)
      .setBody({
                 "array": [
                   1,
                   2,
                   3
                 ],
                 "boolean": true,
                 "null": null,
                 "number": 123,
                 "object": {
                   "a": "b",
                   "c": "d",
                   "e": "f"
                 },
                 "string": "Hello World"
               })
      .addHeader("HeaderKey:HeaderValue")
      .responseDelay(3, SECONDS)
  );
}
```

Imagine with the huge json responses. Your test method will be hard to
readable. Even though it's not so readable with the small json responses.

In order to make it more readable, you can use fixtures.

Create a fixture yaml file under resources/fixtures

src
- test
-- java
-- resources
---- fixtures
------- foo_success.yaml

```yaml
statusCode : 200
delay: 0
headers:
- 'Auth:auth'
- 'key:value'
body: >
    {
      "array": [
        1,
        2,
        3
      ],
      "boolean": true,
      "null": null,
      "number": 123,
      "object": {
        "a": "b",
        "c": "d",
        "e": "f"
      },
      "string": "Hello World"
    }
```

```java
@Rule public MockWebServerPlus server = new MockWebServerPlus();

@Test public void beatifulTest() {
  server.enqueue(Fixtures.FOO_SUCCESS);
}
```