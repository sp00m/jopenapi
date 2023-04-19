import com.github.sp00m.jopenapi.DtoGenerator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        OpenAPI openAPI = new OpenAPIV3Parser().read("https://petstore3.swagger.io/api/v3/openapi.json");
        openAPI
                .getComponents()
                .getSchemas()
                .forEach((name, schema) -> {
                    new DtoGenerator("com.petstore", name, schema).run();
                });
    }

}
