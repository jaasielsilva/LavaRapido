package br.com.lavajato.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve arquivos da pasta "uploads" no filesystem
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
