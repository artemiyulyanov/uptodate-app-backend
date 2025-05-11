package me.artemiyulyanov.uptodate.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Uptodate API", version = "v1.0.0"),
        servers = @Server(url = "https://artemyulyanov.com", description = "Production server")
)
public class SwaggerConfig {
}