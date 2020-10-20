package com.brilliance.netsign.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: WeiBingtao/13156050650@163.com
 * @Version: 1.0
 * @Description:
 * @Date: 2019/2/24 16:55
 */
@Configuration("swagger")
@EnableSwagger2
public class Swagger2 {
    @Autowired
    SwaggerInfo swaggerInfo;
    @Bean
    public Docket createRestApi() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name(swaggerInfo.getParameterName())
                .description(swaggerInfo.getParameterDescription())
                .modelRef(new ModelRef(swaggerInfo.getParameterType()))
                .parameterType(swaggerInfo.getType())
                .required(false)
                .build();
        pars.add(tokenPar.build());

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName(swaggerInfo.getGroupName())
                .apiInfo(apiInfo());
        ApiSelectorBuilder apiSelectorBuilder = docket.select();
        if(StringUtils.isNotBlank(swaggerInfo.getBasePackage())){
            apiSelectorBuilder = apiSelectorBuilder.apis(RequestHandlerSelectors.basePackage(swaggerInfo.getBasePackage()));
        }
        if(StringUtils.isNotBlank(swaggerInfo.getAntPath())){
            apiSelectorBuilder = apiSelectorBuilder.paths(PathSelectors.ant(swaggerInfo.getAntPath()));
        }
        return apiSelectorBuilder.build().globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerInfo.getTitle())
                .description(swaggerInfo.getDescription())
                .contact(new Contact(swaggerInfo.getUserName(),null,swaggerInfo.getEmail()))
                .version(swaggerInfo.getVersion())
                .build();
    }
}
