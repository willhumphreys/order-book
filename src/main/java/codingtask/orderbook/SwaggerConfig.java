package codingtask.orderbook;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("order book")
                .description("<ul><li>The order book starts open. In this state orders can be added.</li><li>Once all orders have been added the order book can be closed.</li><li>Executions can now be added. When the quantity of executions matches the valid order quantity the book is executed.</li><li>Specific orders can now be retrieved to view details such as the price the order was filled at.</li><li>The order book can now be opened again the process repeated.</li></ul>")
                .version("1.0")
                .build();
    }
}