package com.custom.blockchain.configuration;

import static com.google.common.collect.Lists.*;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {
	
	@Value("${monitor.swagger.app.name}")
    private String title;

    @Value("${monitor.swagger.app.terms-of-service-url}")
    private String termsOfServiceUrl;

    @Value("${monitor.swagger.app.license}")
    private String license;

    @Value("${monitor.swagger.app.license-url}")
    private String licenseUrl;

    @Value("${monitor.swagger.app.version}")
    private String version;

    /**
     * 
     * @return SPB Docket
     */
    @Bean
    public Docket v1Api() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("1").apiInfo(apiInfo()).useDefaultResponseMessages(false)
                .globalResponseMessage(org.springframework.web.bind.annotation.RequestMethod.POST, getMessages())
                .securitySchemes(newArrayList(apiKey())).securityContexts(newArrayList(securityContext())).select()
                .apis(RequestHandlerSelectors.basePackage("com.matera.acs.controller.api.v1")).build();
    }

    /**
     * Create docket for system actuator endpoints
     * 
     * @return system Docket
     */
    @Bean
    public Docket secureApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("system").apiInfo(apiInfo()).useDefaultResponseMessages(false)
                .globalResponseMessage(org.springframework.web.bind.annotation.RequestMethod.POST, getMessages())
                .securitySchemes(newArrayList(apiKey())).securityContexts(newArrayList(securityContext())).select()
                .apis(RequestHandlerSelectors.any()).paths(PathSelectors.regex("/system/(.*)")).build();
    }

    /**
     * Set default messages and http codes
     * 
     * @return List of ResponseMessages
     */
    private List<ResponseMessage> getMessages() {
        return Arrays.asList(new ResponseMessageBuilder().code(200).message("OK").responseModel(new ModelRef("RestResponseDTO")).build(),
                new ResponseMessageBuilder().code(400).message("Bad request").responseModel(new ModelRef("RestResponseDTO")).build(),
                new ResponseMessageBuilder().code(401).message("Unauthorized").responseModel(new ModelRef("RestResponseDTO")).build(),
                new ResponseMessageBuilder().code(403).message("Forbidden").responseModel(new ModelRef("RestResponseDTO")).build(),
                new ResponseMessageBuilder().code(500).message("Internal server error").responseModel(new ModelRef("RestResponseDTO")).build());
    }

    /**
     * Create headers for Authorization
     * 
     * @return created ApiKey 
     */
    private ApiKey apiKey() {
        return new ApiKey("Authorization", "token", "header");
    }

    /**
     * 
     * 
     * @return
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/.*")).build();
    }

    /**
     * 
     * @return
     */
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("trusted", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(new SecurityReference("Authorization", authorizationScopes));
    }

    /**
     * Configuration of API informations using properties
     * 
     * @return created ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(title).description("")
                .termsOfServiceUrl(termsOfServiceUrl).license(license).licenseUrl(licenseUrl).version(version).build();
    }
}
