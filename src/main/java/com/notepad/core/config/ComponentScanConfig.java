package com.notepad.core.config;

import com.notepad.core.config.component.CorsConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CorsConfig.class})
public class ComponentScanConfig {
}
