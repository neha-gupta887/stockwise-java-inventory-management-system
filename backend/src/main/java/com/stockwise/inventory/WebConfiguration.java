package com.stockwise.inventory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override public void addCorsMappings(CorsRegistry registry){registry.addMapping("/api/**").allowedOrigins("http://localhost:5173","http://127.0.0.1:5173","http://localhost:5182","http://127.0.0.1:5182","http://localhost:5183","http://127.0.0.1:5183").allowedMethods("GET","POST","PUT","DELETE","OPTIONS").allowedHeaders("*");}
}
