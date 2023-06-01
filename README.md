# jopenapi

Playground to generate Java objects from an OpenAPI Specification.

Obviously largely inspired by [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), but:

- Java only
- Immutability
- Null-safe
- Jackson-ready
- Validation-ready

1. [generator](generator) generates source files from an OpenAPI Specification document,
2. [packager](packager) receives these source files and package them.

Relies on:

- `io.swagger.parser.v3:swagger-parser` to parse the OpenAPI Specification,
- `com.github.javaparser:javaparser-symbol-solver-core` to generate Java objects.
