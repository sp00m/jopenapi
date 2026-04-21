# jopenapi

Generate immutable, null-safe Java records from OpenAPI schemas — ready for Jackson 3 and Jakarta Validation.

## Design

jopenapi produces Java `record` types that enforce a few strict rules at the type level:

| Concern | Approach |
|---|---|
| **Immutability** | Records are used throughout. Collection fields (`List`, `Set`, `Map`) are wrapped into `Collections.unmodifiableX` in the compact constructor. |
| **No nulls** | Optional properties without a default become `Optional<T>`. Null collections/optionals are replaced by their empty equivalents. |
| **Use primitives** | Required fields (or optional-with-default) that map to a Java primitive (`int`, `long`, `boolean`, …) use the primitive type for a better developer experience. |
| **Explicit null == no value** | An explicit `null` in JSON is treated identically to an absent value. |

### Deserialization via `@JsonCreator`

Every generated record includes a `@JsonCreator` static factory method. The factory:

- Accepts **boxed types** for all parameters (e.g. `Integer` instead of `int`) so Jackson can distinguish missing values from zero/false.
- **Validates required fields** — throws `IllegalArgumentException` when a required property is `null`.
- **Applies defaults** — uses `Objects.requireNonNullElse(value, default)` for optional properties with a schema-level default.
- **Wraps optionals** — uses `Optional.ofNullable(value)` for optional properties without a default.
- **Excludes read-only fields** — read-only properties are not part of the factory signature; they receive a safe default (`Optional.empty()`, empty collection, …).

The compact constructor remains a safety net: it ensures collection immutability and converts null `Optional` references to `Optional.empty()`.

### Enum defaults

When an enum schema has a `default`, that value is used as a fallback for invalid inputs. If an optional property references such an enum without a local default, the enum's default is inherited automatically.

## Prerequisites

- **Generated code** targets **Java 17+**.
- **OpenAPI 3.x** schemas (YAML or JSON).
- **Jackson 3** annotations (`@JsonCreator`, `@JsonProperty`, `@JsonUnwrapped`, …).
- **Jakarta Validation** annotations (`@DecimalMin`, `@DecimalMax`, `@Size`, `@Pattern`).

The generated records also carry Lombok's `@Builder` and `@With` annotations. jopenapi runs a delombok pass before writing the final sources, so the output is **Lombok-agnostic** — your project does not need Lombok at runtime.

## Getting started

Download the latest `jopenapi.jar` from the [Releases](https://github.com/sp00m/jopenapi/releases) page.

Requires **Java 17+** to run the tool itself.

## CLI usage

```
Usage: jopenapi [-hV] -i=<inputDir> -o=<outputDir> -p=<packageName>

Generate Java DTOs from OpenAPI schemas.

  -p, --package=<packageName>   Base Java package name for generated classes (e.g. com.example.api).
  -i, --input=<inputDir>        Input directory or single file containing OpenAPI schema(s) (.yml, .yaml, .json). When a single file is provided, DTOs are placed directly in the base package.
  -o, --output=<outputDir>      Output directory for generated Java source files.
  -h, --help                    Show this help message and exit.
  -V, --version                 Print version information and exit.
```

### Example

Given the following schema:

```yaml
MyObject:
  type: object
  required:
    - my_required_int
  properties:
    my_required_int:
      type: integer
    my_optional_int_without_default:
      type: integer
    my_optional_int_with_default:
      type: integer
      default: 42
```

jopenapi generates:

```java
@Builder(toBuilder = true)
@With
public record MyObject(
    @JsonProperty("my_required_int") int myRequiredInt,
    @JsonProperty("my_optional_int_without_default") Optional<Integer> myOptionalIntWithoutDefault,
    @JsonProperty("my_optional_int_with_default") int myOptionalIntWithDefault
) {

    public MyObject {
        myOptionalIntWithoutDefault =
            Objects.requireNonNullElse(myOptionalIntWithoutDefault, Optional.empty());
    }

    @JsonCreator
    public static MyObject create(
        @JsonProperty("my_required_int") Integer myRequiredInt,
        @JsonProperty("my_optional_int_without_default") Integer myOptionalIntWithoutDefault,
        @JsonProperty("my_optional_int_with_default") Integer myOptionalIntWithDefault
    ) {
        if (myRequiredInt == null) {
            throw new IllegalArgumentException("Property 'my_required_int' is required");
        }
        return new MyObject(
            myRequiredInt,
            Optional.ofNullable(myOptionalIntWithoutDefault),
            Objects.requireNonNullElse(myOptionalIntWithDefault, 42)
        );
    }
}
```

Run the tool:

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
