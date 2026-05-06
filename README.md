# jopenapi

> Generate immutable, null-safe Java `record` DTOs from OpenAPI schemas — ready for Jackson 2/3 and Jakarta Validation.

## 📖 Table of contents

- [🎨 Design](#design)
- [⚖️ Comparison with other generators](#comparison-with-other-generators)
- [🚀 Getting started](#getting-started)
  - [✅ Prerequisites](#prerequisites)
  - [💻 CLI usage](#cli-usage)
  - [🔧 Integration patterns](#integration-patterns)

---

## 🎨 Design

jopenapi produces Java `record` types that enforce a few strict rules at the type level:

| Concern | Approach |
|---|---|
| **Immutability** | Records are used throughout. Collection fields (`List`, `Set`, `Map`) are wrapped in `Collections.unmodifiableX` inside the compact constructor. |
| **No nulls** | Optional properties without a default become `Optional<T>`. Null collections and optionals are replaced by their empty equivalents. |
| **Use primitives** | Required fields — or optional fields with a default — that map to a Java primitive (`int`, `long`, `boolean`, …) use the primitive type for a better developer experience. |
| **Explicit null == no value** | An explicit `null` in JSON is treated identically to an absent value. If the property is required, `com.github.jopenapi.support.MissingPropertyException` is thrown. |

### 🔢 Enum defaults

When an enum schema has a `default`, that value is used as a fallback for invalid inputs and a warning is logged. If an optional property references such an enum without a local default, the enum's default is inherited automatically.

When an enum schema has **no** `default`, attempting to deserialize an unknown value throws `com.github.jopenapi.support.InvalidPropertyException`.

### 🗄️ jOOQ integration

If you use [jOOQ](https://www.jooq.org/), you can make generated enums implement `org.jooq.EnumType` by adding an `x-jooq` extension to the schema. This lets jOOQ bind enum values directly to SQL enum columns without any manual mapping.

> **⚠️ Layer pollution:** `x-jooq` couples your API-layer DTOs to a persistence library. While convenient for small projects, it violates clean-architecture boundaries. In larger codebases, consider keeping generated DTOs free of jOOQ concerns and mapping to dedicated persistence types instead.

The extension accepts the following optional fields:

| Field | Type | Description |
|---|---|---|
| `name` | `string` | SQL type name returned by `getName()`. Omit to return `null` (anonymous type). |
| `catalog` | `string` | Catalog name — generates a `getCatalog()` override. Omit to use the jOOQ default. |
| `schema` | `string` | Schema name — generates a `getSchema()` override. Omit to use the jOOQ default. |

**Example schema:**

```yaml
Status:
  type: string
  enum:
    - active
    - inactive
  x-jooq:
    name: status
    catalog: my_catalog
    schema: my_schema
```

**Generated code:**

```java
public enum Status implements EnumType {

    // ...

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public Catalog getCatalog() {
        return DSL.catalog("my_catalog");
    }

    @Override
    public Schema getSchema() {
        return DSL.schema("my_schema");
    }
}
```

> **Note:** your project must have `org.jooq:jooq` on the classpath to compile generated enums that use `x-jooq`.

---

## ⚖️ Comparison with other generators

Two well-known tools already generate Java code from OpenAPI specs: [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) (community fork) and [Swagger Codegen](https://github.com/swagger-api/swagger-codegen) (SmartBear). Both are full-featured SDK generators that produce clients, server stubs, and documentation across dozens of languages. jopenapi does **one thing only** — generate strict, immutable Java DTOs — so the comparison below focuses on the Java model-generation dimension.

| Feature | jopenapi | OpenAPI Generator | Swagger Codegen |
|---|---|---|---|
| **Scope** | DTOs only | Full SDK: clients, servers, docs (50+ languages) | Full SDK: clients, servers, docs (40+ languages) |
| **Model style** | Java `record` (Java 17+) | POJOs with getters/setters (records opt-in, experimental) | POJOs with getters/setters |
| **Immutability** | Built-in; collections wrapped in `Collections.unmodifiableX` | Opt-in `immutableModels` removes setters, but no deep collection immutability | Mutable |
| **Null-safety** | `Optional<T>` for optional fields; null collections → empty; primitives for required fields | Nullable fields remain nullable; no `Optional` support | Nullable fields remain nullable; no `Optional` support |
| **Serialization** | Jackson 2/3 | Jackson, Gson, JSON-B, Moshi, kotlinx.serialization | Jackson, Gson |
| **Validation** | Jakarta Validation (`@DecimalMin`, `@Size`, `@Pattern`, …) | Jakarta/Javax Validation (opt-in) | Javax Validation (opt-in) |
| **Builders** | `@Builder` + `@With` (delombok'd to plain Java) | Opt-in via `generateBuilders` (fluent setters) | Not built-in |
| **OpenAPI versions** | 3.x | 2.0, 3.0, 3.1 | 2.0, 3.0 |
| **Build integration** | CLI fat JAR; Maven/Gradle via exec plugins | Dedicated Maven & Gradle plugins, CLI, Docker | Dedicated Maven & Gradle plugins, CLI, Docker |
| **Customization** | None — AST-based generation (JavaParser) | Mustache/Handlebars templates, fully overridable | Mustache templates, fully overridable |
| **Community** | New project | Very active, large community | Low activity on 3.x branch |

**TL;DR:** if you need a full client/server SDK, multi-language support, or fine-grained template control, reach for [OpenAPI Generator](https://openapi-generator.tech/) or [Swagger Codegen](https://swagger.io/tools/swagger-codegen/). If you only need Java DTOs that are immutable and null-safe out of the box, give jopenapi a try.

---

## 🚀 Getting started

Download the latest `jopenapi.jar` from the [Releases](https://github.com/sp00m/jopenapi/releases) page.

### ✅ Prerequisites

- **Java 17+** — both to run the tool and as the target for generated code.
- **OpenAPI 3.x** schemas (YAML or JSON).
- **Jackson 2 or 3** — generated code uses `@JsonCreator`, `@JsonProperty`, `@JsonUnwrapped`, etc. Jackson 3 still uses `jackson-annotations` 2.x, so the output is compatible with both versions.
- **Jakarta Validation** — generated code uses `@DecimalMin`, `@DecimalMax`, `@Size`, `@Pattern`, etc.

> **Jackson 2 users:** register the `Jdk8Module` (`com.fasterxml.jackson.datatype:jackson-datatype-jdk8`) on your `ObjectMapper` so that `Optional` fields serialize/deserialize correctly. Jackson 3 includes this support in `jackson-databind` out of the box.

The generated records carry Lombok's `@Builder` and `@With` annotations. jopenapi runs a delombok pass before writing the final sources, so the output is **Lombok-agnostic** — your project does not need Lombok at runtime.

### 💻 CLI usage

```
Usage: jopenapi [-hV] -i=<inputDir> -o=<outputDir> -p=<packageName>

Generate Java DTOs from OpenAPI schemas.

  -p, --package=<packageName>   Base Java package name for generated classes (e.g. com.example.api).
  -i, --input=<inputDir>        Input directory or single file containing OpenAPI schema(s) (.yml, .yaml, .json).
                                When a single file is provided, DTOs are placed directly in the base package.
  -o, --output=<outputDir>      Output directory for generated Java source files.
  -h, --help                    Show this help message and exit.
  -V, --version                 Print version information and exit.
```

#### 📝 Example

Given this schema:

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
    static MyObject create(
        @JsonProperty("my_required_int") Integer myRequiredInt,
        @JsonProperty("my_optional_int_without_default") Integer myOptionalIntWithoutDefault,
        @JsonProperty("my_optional_int_with_default") Integer myOptionalIntWithDefault
    ) {
        if (myRequiredInt == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("my_required_int");
        }
        return new MyObject(
            myRequiredInt,
            Optional.ofNullable(myOptionalIntWithoutDefault),
            Objects.requireNonNullElse(myOptionalIntWithDefault, 42)
        );
    }
}
```

#### 🔢 Exit codes

| Code | Meaning |
|------|---|
| `0` | Success |
| `1` | Generation error (bad input, I/O failure) |
| `2` | Usage error (missing or invalid arguments) |

### 🔧 Integration patterns

#### 🐚 Shell / CI

Run the fat JAR directly from any script or CI pipeline:

```bash
java -jar jopenapi.jar \
  --package com.example.api \
  --input  ./schemas \
  --output ./src/main/java
```

#### 🪶 Maven

Use `maven-antrun-plugin` to download the JAR from the GitHub Release, then `exec-maven-plugin` to invoke it during `generate-sources`:

```xml
<properties>
    <jopenapi.version>0.0.1</jopenapi.version>
    <jopenapi.url>https://github.com/sp00m/jopenapi/releases/download/v${jopenapi.version}/jopenapi.jar</jopenapi.url>
    <jopenapi.jar>${project.build.directory}/jopenapi/jopenapi-v${jopenapi.version}.jar</jopenapi.jar>
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

#### 🐘 Gradle (Kotlin DSL)

Download the JAR and run it as a `JavaExec` task wired before compilation:

```kotlin
val jopenapiVersion = "0.0.1"
val jopenapiJar = layout.buildDirectory.file("jopenapi/jopenapi-v${jopenapiVersion}.jar")

val downloadJopenapi by tasks.registering {
    val url = "https://github.com/sp00m/jopenapi/releases/download/v${jopenapiVersion}/jopenapi.jar"
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
        "--input",  file("src/main/openapi").absolutePath,
        "--output", layout.buildDirectory.dir("generated-sources/jopenapi").get().asFile.absolutePath
    )
}

sourceSets["main"].java.srcDir(layout.buildDirectory.dir("generated-sources/jopenapi"))

tasks.named("compileJava") {
    dependsOn(generateDtos)
}
```
