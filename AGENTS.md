# AGENTS.md

Guide for AI coding agents working on the jopenapi codebase.

## What this project does

jopenapi generates immutable, null-safe Java `record` DTOs from OpenAPI 3.x schemas. It ships as a self-contained fat JAR (built with `maven-shade-plugin`) and is designed to be invoked from any build tool or CI pipeline.

## Core rules

These rules are fundamental to the project and must never be bypassed:

1. **Immutability** — generated DTOs are Java records. Collection properties (`List`, `Set`, `Map`) are wrapped into `Collections.unmodifiableX` in the record's compact constructor.

2. **No nulls** — optional properties without a default value (and that are not collections) are wrapped in `java.util.Optional`. If a record receives `null` for a collection or an `Optional`, the compact constructor replaces it with `Collections.emptyX()` or `Optional.empty()` via `Objects.requireNonNullElse`.

3. **Use primitives** — when a property is required (or optional but has a default), and its type maps to a Java primitive, use the primitive instead of the boxed type.

4. **Enum defaults** — when an enum has a `default`, that default is used for invalid values. When an optional property references such an enum without a local default, it inherits the enum's default.

5. **Explicit null == no value** — an explicit `null` in JSON behaves identically to an absent value.

## Architecture

The project is built in three phases:

### Phase 1 — Read (`com.github.sp00m.jopenapi.read`)

Parses OpenAPI specs and produces value objects representing schemas.

| Class | Responsibility |
|---|---|
| `OpenApiReader` | Scans an input directory for `.yml`/`.yaml`/`.json` files (or accepts a single schema file directly), parses each, and links cross-schema references (interfaces for `oneOf`, enum defaults). When a single file is given, DTOs are placed directly in the base package with no subpackage. |
| `OpenApiComponentReader` | Reads a single OpenAPI component schema and produces a `JavaTypeDefinition`. Wraps simple/collection types in `JavaValueRecordDefinition`. |
| `OpenApiSchemaReader` | Recursive schema reader. Handles `string`, `number`, `integer`, `boolean`, `array`, `object`, `$ref`, `allOf`, `oneOf`, `enum`. Returns a `JavaType`. |
| `JavaFieldAnnotator` | Enum of annotation strategies (`MIN`, `MAX`, `SIZE`, `PATTERN`, `JSON_PROPERTY`, `JSON_UNWRAPPED`). Each adds Jakarta Validation or Jackson annotations to a record parameter. |

**Key value objects** (`read.vo`):

- `JavaType` — immutable type descriptor: full name, optional `JavaTypeDefinition`, field annotators, default value + decorator, collection flag, description.
- `JavaFieldDefinition` — a field: `OpenApiProperty` + Java name + `JavaType`.
- `OpenApiProperty` — original property name, schema, optional flag.
- `JavaRecordDefinition`, `JavaEnumDefinition`, `JavaValueRecordDefinition`, `JavaInterfaceDefinition` — type definitions that implement `JavaTypeDefinition`.

### Phase 2 — Generate (`com.github.sp00m.jopenapi.generate`)

Produces Java source code from the value objects using JavaParser.

| Class | Responsibility |
|---|---|
| `JavaGenerator` | Orchestrates generation: dispatches to the correct type generator, adds JavaDoc, sets the package declaration, sorts imports. |
| `JavaRecordGenerator` | Generates `record` declarations with `@With`, `@Builder(toBuilder = true)`, compact constructor, and `@JsonCreator` static factory method. |
| `JavaEnumGenerator` | Generates `enum` declarations with `@JsonValue`, `@JsonCreator`, optional default-fallback logic, and optional `org.jooq.EnumType` implementation when `x-jooq` is present on the schema. |
| `JavaValueRecordGenerator` | Generates wrapper records for simple/collection top-level types (`SimpleInteger`, `SimpleArray`, …). |
| `JavaInterfaceGenerator` | Generates `sealed interface` declarations with `@JsonTypeInfo` and `@JsonSubTypes` for `oneOf`. |

### Phase 3 — Write (`com.github.sp00m.jopenapi.write`)

Writes generated sources to disk, optionally running a delombok pass.

| Class | Responsibility |
|---|---|
| `JavaFileWriter` | Writes `.java` files. When `delombok=true`, writes to a temp directory first, then runs `Delomboker`. |
| `Delomboker` | Invokes `java -jar lombok.jar delombok` as a subprocess to expand Lombok annotations into plain Java. |

`Main.run()` also copies the bundled `/support/*.java` resources into `com/github/jopenapi/support/` inside the output directory, so consumers have no external dependency for these classes.

## `@JsonCreator` factory method — how it works

`JavaRecordGenerator` generates a package-private `static` method named `create` annotated with `@JsonCreator` for each record. This is the core deserialization entry point. It is intentionally not `public` so that developers cannot call it directly — Jackson invokes it via reflection during deserialization.

### Parameter rules

| Field kind | Record param type | Factory param type | Factory body |
|---|---|---|---|
| Required primitive | `int`, `long`, `boolean`, … | `Integer`, `Long`, `Boolean`, … | `if (x == null) throw new com.github.jopenapi.support.MissingPropertyException(…)` then auto-unbox |
| Required reference | `String`, `LocalDate`, custom type | same | `if (x == null) throw new com.github.jopenapi.support.MissingPropertyException(…)` |
| Optional without default | `Optional<X>` | `X` | `Optional.ofNullable(x)` |
| Optional with default (primitive-able) | `int`, `long`, `boolean`, … | `Integer`, `Long`, `Boolean`, … | `Objects.requireNonNullElse(x, default)` |
| Optional with default (reference) | `String`, `LocalDate`, enum, … | same | `Objects.requireNonNullElse(x, default)` |
| Collection (any) | `List<X>`, `Set<X>`, `Map<K,V>` | same | passed through — compact constructor handles null/immutability |
| Read-only | `Optional<X>` or collection | *excluded from factory* | receives `Optional.empty()` or empty collection |
| Write-only | same as above rules | same + `access = WRITE_ONLY` on `@JsonProperty` | follows above rules |
| `@JsonUnwrapped` (allOf) | FQ type | same, `@JsonUnwrapped` annotation | null-check if required |

### Compact constructor

The compact constructor handles only two concerns:

1. **Collections**: `field = field == null ? emptyX() : unmodifiableX(field);`
2. **Optionals**: `field = Objects.requireNonNullElse(field, Optional.empty());`

It does **not** handle defaults — that is the factory's job.

## Naming conventions

`Names` converts OpenAPI names to Java identifiers:

- `toClassName("my_object")` → `MyObject`
- `toFieldName("my_field")` → `myField`
- `toEnumValue("my value")` → `MY_VALUE`
- `toPackageName("my-file.yml")` → `my_file`

Leading digits are spelled out (`1foo` → `OneFoo`). Reserved words get a `Value` suffix.

## Testing

`ExampleTest` is the primary test. It:

1. Runs `Main.run(…)` with `delombok=false` to a temp directory.
2. Byte-for-byte compares every generated file against expected files in `src/test/resources/example/generated/`.

**To update expected files after a code change:**

1. Make your code changes.
2. Run `mvn compile`.
3. Run `ExampleTest.main` to regenerate the expected files in-place:
   ```
   mvn exec:java -Dexec.mainClass="com.github.sp00m.jopenapi.ExampleTest" -Dexec.classpathScope="test"
   ```
   This calls `Main.run(…)` pointing directly at `src/test/resources/example/generated`, overwriting the expected files.
4. **Verify the generated DTOs** — do not blindly accept the output. Check that the changes in the generated files match what you actually intended (correct types, annotations, null-handling, defaults, etc.).
5. Run `mvn test` and verify all tests pass.

Test schemas live in `src/test/resources/example/schemas/` and cover integers, booleans, strings, numbers, arrays, maps, enums, refs, allOf, oneOf, anyOf, not, defaults, readwrite, and javadoc.

## Key dependencies

| Dependency | Purpose |
|---|---|
| `io.swagger.parser.v3:swagger-parser` | Parse OpenAPI specs |
| `com.github.javaparser:javaparser-symbol-solver-core` | Generate Java AST and print source |
| `com.fasterxml.jackson.core:jackson-annotations` | Annotations used in generated code |
| `jakarta.validation:jakarta.validation-api` | Validation annotations used in generated code |
| `org.projectlombok:lombok` | `@Builder`, `@With` in pre-delombok code; also used in the tool's own source |
| `info.picocli:picocli` | CLI argument parsing |
| `org.jooq:jooq` | `EnumType`, `Catalog`, `Schema`, `DSL` — used in generated code when `x-jooq` is present |

## After every code change

1. **Run the tests** — `mvn test`. If generation logic changed, regenerate expected files first (see [Testing](#testing)).
2. **Update the docs** — check whether the following need updating:
   - **`AGENTS.md`** — architecture tables, class responsibilities, parameter rules, pitfalls.
   - **`README.md`** — CLI usage block (option descriptions, examples), Maven/Gradle snippets.

## Common pitfalls

- **Lombok is build-time only.** The generated DTOs must be Lombok-agnostic after delombok. Never introduce a Lombok annotation in the generated output that `delombok` cannot expand.
- **JavaParser string bodies.** Method bodies in `JavaRecordGenerator` are built as raw strings and parsed via `StaticJavaParser.parseBlock()`. Default values from `JavaType.getDefaultValue()` are already formatted as valid Java expressions (e.g. `java.time.LocalDate.parse("2020-06-30")`).
- **Fully-qualified vs. short names.** Default value expressions use FQN (e.g. `java.util.UUID.fromString(…)`) because no import is added for them. `Objects`, `Optional`, and `Collections` use short names with explicit `compiler.addImport(…)` calls.
- **Test comparison is byte-exact.** Any whitespace, import order, or annotation argument difference will fail the test. If you change generation logic, always regenerate the expected files.
- **`@JsonUnwrapped` + `@JsonCreator`.** This works since Jackson 2.19. `@JsonUnwrapped` fields in allOf records are included in the factory method parameters with the `@JsonUnwrapped` annotation (not `@JsonProperty`).
- **Read-only properties are treated as optional.** `isPropertyOptional()` in `OpenApiSchemaReader` returns `true` when `readOnly` is set, ensuring these fields become `Optional<T>` or collections. They are excluded from the `@JsonCreator` factory.
- **`x-jooq` and `JavaEnumDefinition`.** `JavaEnumDefinition` stores the full `Schema<?>` object (mutable) so that `JavaEnumGenerator` can read `getExtensions()` for `x-jooq`. The `description()` method delegates to `schema.getDescription()` to preserve the `JavaTypeDefinition` interface contract. Consumers of `JooqEnumDefault`/`JooqEnumCustom`-style enums must have `org.jooq:jooq` on their classpath.

