# jopenapi

Generate immutable, null-safe, Jackson-ready, validation-ready Java DTOs from OpenAPI schemas.

Inspired by [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), but focused on:

- Java only
- Immutability
- Null-safe
- Jackson-ready
- Validation-ready

Relies on:

- `io.swagger.parser.v3:swagger-parser` to parse the OpenAPI specification
- `com.github.javaparser:javaparser-symbol-solver-core` to generate Java source files

## Getting started

Download the latest `jopenapi.jar` from the [Releases](https://github.com/sp00m/jopenapi/releases) page.

Requires **Java 21+**.

## CLI usage

```
Usage: jopenapi [-hV] -i=<inputDir> -o=<outputDir> -p=<packageName>

Generate Java DTOs from OpenAPI schemas.

  -p, --package=<packageName>   Base Java package name for generated classes (e.g. com.example.api).
  -i, --input=<inputDir>        Input directory containing OpenAPI schema files (.yml, .yaml, .json).
  -o, --output=<outputDir>      Output directory for generated Java source files.
  -h, --help                    Show this help message and exit.
  -V, --version                 Print version information and exit.
```

### Example

```bash
java -jar jopenapi.jar \
  --package com.example.api \
  --input ./schemas \
  --output ./src/main/java
```

### Exit codes

| Code | Meaning                                   |
|------|-------------------------------------------|
| 0    | Success                                   |
| 1    | Generation error (bad input, I/O failure) |
| 2    | Usage error (missing/invalid arguments)   |

## Integration patterns

### Shell / CI

Run the fat JAR directly in any script or CI pipeline:

```bash
java -jar jopenapi.jar \
  --package com.example.api \
  --input ./schemas \
  --output ./src/main/java
```

### Maven

Use `maven-antrun-plugin` to download the fat JAR from the GitHub Release, then `exec-maven-plugin` to run it during `generate-sources`:

```xml
<properties>
    <jopenapi.version>0.1.0</jopenapi.version>
    <jopenapi.url>https://github.com/sp00m/jopenapi/releases/download/v${jopenapi.version}/jopenapi.jar</jopenapi.url>
    <jopenapi.jar>${project.build.directory}/jopenapi/jopenapi.jar</jopenapi.jar>
    <jopenapi.package>com.example.api</jopenapi.package>
    <jopenapi.input>${project.basedir}/src/main/openapi</jopenapi.input>
    <jopenapi.output>${project.build.directory}/generated-sources/jopenapi</jopenapi.output>
</properties>

<build>
    <plugins>

        <!-- 1. Download jopenapi.jar from the GitHub Release -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-antrun-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
                <execution>
                    <id>download-jopenapi</id>
                    <phase>initialize</phase>
                    <goals>
                        <goal>run</goal>
                    </goals>
                    <configuration>
                        <target>
                            <mkdir dir="${project.build.directory}/jopenapi"/>
                            <get src="${jopenapi.url}"
                                 dest="${jopenapi.jar}"
                                 skipexisting="true"/>
                        </target>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <!-- 2. Run jopenapi to generate Java sources -->
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.5.0</version>
            <executions>
                <execution>
                    <id>generate-dtos</id>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>exec</goal>
                    </goals>
                    <configuration>
                        <executable>java</executable>
                        <arguments>
                            <argument>-jar</argument>
                            <argument>${jopenapi.jar}</argument>
                            <argument>--package</argument>
                            <argument>${jopenapi.package}</argument>
                            <argument>--input</argument>
                            <argument>${jopenapi.input}</argument>
                            <argument>--output</argument>
                            <argument>${jopenapi.output}</argument>
                        </arguments>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <!-- 3. Add the generated sources to the compile path -->
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>build-helper-maven-plugin</artifactId>
            <version>3.6.0</version>
            <executions>
                <execution>
                    <id>add-generated-sources</id>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>add-source</goal>
                    </goals>
                    <configuration>
                        <sources>
                            <source>${jopenapi.output}</source>
                        </sources>
                    </configuration>
                </execution>
            </executions>
        </plugin>

    </plugins>
</build>
```


### Gradle (Kotlin DSL)

Download the JAR and execute it as a `JavaExec` task wired before compilation:

```kotlin
val jopenapiJar = layout.buildDirectory.file("jopenapi/jopenapi.jar")

val downloadJopenapi by tasks.registering {
    val url = "https://github.com/sp00m/jopenapi/releases/download/v0.1.0/jopenapi.jar"
    val dest = jopenapiJar.get().asFile
    outputs.file(dest)
    doLast {
        dest.parentFile.mkdirs()
        java.net.URI(url).toURL().openStream().use { it.copyTo(dest.outputStream()) }
    }
}

val generateDtos by tasks.registering(JavaExec::class) {
    dependsOn(downloadJopenapi)
    classpath(jopenapiJar)
    args(
        "--package", "com.example.api",
        "--input", file("src/main/openapi").absolutePath,
        "--output", layout.buildDirectory.dir("generated-sources/jopenapi").get().asFile.absolutePath
    )
}

sourceSets["main"].java.srcDir(layout.buildDirectory.dir("generated-sources/jopenapi"))

tasks.named("compileJava") {
    dependsOn(generateDtos)
}
```
