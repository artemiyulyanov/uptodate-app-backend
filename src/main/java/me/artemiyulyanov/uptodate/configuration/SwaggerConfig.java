package me.artemiyulyanov.uptodate.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(title = "Uptodate API", version = "v1.0.0"),
        servers = @Server(url = "https://artemyulyanov.com", description = "Production server")
)
public class SwaggerConfig {
}