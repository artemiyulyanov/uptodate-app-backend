package me.artemiyulyanov.uptodate.configuration;

import com.github.slugify.Slugify;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextConfig {
    @Bean
    public Slugify slugify() {
        return new Slugify();
    }
}