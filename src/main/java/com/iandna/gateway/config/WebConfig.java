package com.iandna.gateway.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.iandna.gateway.config.interceptor.CommonInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{ 
	
	@Autowired
	private CommonInterceptor commInterceptor;
	
	// CommonInterceptor 태울 URL 정의
	private static final List<String> COMMON_URL_PATTERNS = Arrays.asList("/gateway/hikVision/*", "/board", "/user");
	
	// 정적 리소스 정의
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
	  	      "classpath:/META-INF/resources/",
	  	      "classpath:/static/",
	  	      "classpath:/public/",
	  	      "classpath:/META-INF/resources/webjars/",
	  	      "classpath:/resources/"
	  	      //"/resources/"
	};
	
	// 인터셉터별 check url 기술
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(commInterceptor).addPathPatterns(COMMON_URL_PATTERNS);
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/resources/**").addResourceLocations("resources/");
	    registry.addResourceHandler("/resource/**").addResourceLocations("WEB-INF/resources/");
		if (!registry.hasMappingForPattern("/**")) {
		      registry.addResourceHandler("/**").addResourceLocations(
		          CLASSPATH_RESOURCE_LOCATIONS);
		}

        registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
