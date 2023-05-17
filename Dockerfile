FROM maven:3-amazoncorretto-17

COPY pom.xml /jopenapi/
COPY generator/pom.xml /jopenapi/generator/
COPY generator/src /jopenapi/generator/src
COPY packager/pom.xml /jopenapi/packager/

WORKDIR /jopenapi/
ENTRYPOINT ["mvn", "clean", "package", "-DskipTests"]
