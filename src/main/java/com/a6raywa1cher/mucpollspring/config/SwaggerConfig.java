package com.a6raywa1cher.mucpollspring.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		List<SecurityScheme> schemeList = new ArrayList<>();
		schemeList.add(new BasicAuth("Realm"));
		ApiInfo apiInfo = new ApiInfoBuilder()
				.title("mucpoll-spring")
				.license("MIT License")
				.licenseUrl("https://github.com/monkey-underground-coders/mucpoll-spring/blob/master/LICENSE")
				.build();

		return new Docket(DocumentationType.SWAGGER_2)
				.produces(Collections.singleton("application/json"))
				.consumes(Collections.singleton("application/json"))
				.host("")
				.securitySchemes(schemeList)
				.useDefaultResponseMessages(true)
				.apiInfo(apiInfo)
				.securityContexts(Arrays.asList(securityContext()))
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.a6raywa1cher.mucpollspring.rest"))
				.paths(PathSelectors.any())
				.build();
	}

	private SecurityContext securityContext() {
		//noinspection Guava
		return SecurityContext.builder()
				.securityReferences(defaultAuth())
				.forPaths(Predicates.not(Predicates.or(
						PathSelectors.ant("/user/reg"),
						PathSelectors.ant("/comment/**"))))
				.build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope
				= new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{authorizationScope};
		return Collections.singletonList(
				new SecurityReference("Realm", authorizationScopes));
	}
}
