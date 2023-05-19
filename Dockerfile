FROM maven:3-amazoncorretto-17

COPY pom.xml /jopenapi/app/
COPY generator/pom.xml /jopenapi/app/generator/
COPY generator/src /jopenapi/app/generator/src
COPY packager/pom.xml /jopenapi/app/packager/

WORKDIR /jopenapi/app/
ENTRYPOINT ["mvn", "clean", "package", "-DskipTests"]
