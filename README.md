### mockwebserver +

#### Issue
MockWebServer is a great tool for mocking network requests/responses.
In order to add response, you need to set MockResponse body along with all
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
  
  // execute request
  // assert
  // verify
}
```

Imagine it with huge json responses. It will obscure the method content and will be barely readable.


#### Solution
In order to make it more readable, you can use fixtures. Move away your response to the fixtures and just reference them.
MockWebServerPlus is a wrapper which contains MockWebServer with fixtures feature.

##### Create a fixture yaml file under resources/fixtures

```
src
├── test
│   ├── java
│   ├── resources
│   │   ├── fixtures
│   │   │   ├── foo_success.yaml
│   │   │   ├── foo_failure.yaml
```

```yaml
statusCode : 200       // as the name says
delay: 0               // delays the response
headers:               // adds to the response
- 'Auth:auth'
- 'key:value'
body: >                // can be any text, json, plain etc. Use > letter for scalar text
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

### Use the file name to reference it. That's it!

```java
@Rule public MockWebServerPlus server = new MockWebServerPlus();

@Test public void readableTest() {
  server.enqueue("foo_success");
  
  // execute request
  // assert
  // verify
}
```

#### Use the generated Fixtures.java to reference your fixtures. Read the Generate Fixtures.java part
```java
server.enqueue(Fixtures.FOO_SUCCESS);
```

### Enqueue multiple response
```java
server.enqueue(Fixtures.FOO_SUCCESS, Fixtures.USER_REGISTER_SUCCESS);
```

### Generate Fixtures.java
You can always use plain text to reference your fixtures.

```java
server.enqueue("foo_success");
```

but you can also generate Fixtures.java file to have all of them with a task. This will make your code more type-safe.
Put the following task into your build.gradle file and execute it when you add/modify your fixture resources.

```groovy
task generateFixtures(dependsOn: copyTestResources) << {
  def directory = projectDir.path + '/src/test/java'
  new File(directory + '/fixtures').mkdir()
  def path = directory + "/fixtures/Fixtures.java"

  def builder = '' << ''
  builder.append("package fixtures;\n\n")
  builder.append("public class Fixtures {\n\n")
  builder.append("  private Fixtures() {\n")
  builder.append("    //no instance\n")
  builder.append("  }\n\n")

  def resources = android.sourceSets.test.resources.srcDirs.getAt(0)
  if (resources.size() > 0) {
    resources.eachDirMatch("fixtures") { dir ->
      def fixturesFile = dir
      fixturesFile.eachFile { file ->
        def fileName = file.name.split('\\.')[0]
        builder.append("  public static final String ")
            .append(fileName.toUpperCase())
            .append(" = ")
            .append('\"')
            .append(fileName)
            .append('\";\n')
      }
    }
  }
  builder.append("}\n")

  new File(path).write(builder.toString())
}

```

Above solution will generate Fixtures.java when you execute it manually. But you might forget to execute it, you can
make it dependent for any task to make it automated. Whenever assembleDebug is executed, it will also execute this task

```groovy
assembleDebug.dependsOn generateFixtures
```

### Dependency
```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/"}
}
dependencies {
    testCompile 'com.orhanobut:mockwebserverplus:0.8.1-SNAPSHOT'
}
```

#### Other proxy methods
```java
MockWebServerPlus.server()         // returns MockWebServer instance
MockWebServerPlus.takeRequest()    // returns RecordedRequest
MockWebServerPlus.url(String path) // returns url to execute
MockWebServerPlus.setDispatcher(Dispatcher dispatcher)  // any custom dispatcher
MockWebServerPlus.enqueue(SocketPolicy socketPolicy)    // Useful for network errors, such as DISCONNECT etc
```

### Get the fixture object
You may want to handle the data inside fixture differently. You can use Fixture object which contains all information that resides in yaml file.
```java
Fixture fixture = Fixture.parseFrom(Fixtures.SIMPLE);
```

### For non-android modules
For non-android modules, you may need to add the following tasks to copy your resources into classes dir
```groovy
task copyTestResources(type: Copy) {
  from sourceSets.test.resources
  into sourceSets.test.output.classesDir
}
```

Also notice that accessing sourceSets should be without android.

#### Credits
[MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) from Square

### License
<pre>
Copyright 2016 Orhan Obut

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
