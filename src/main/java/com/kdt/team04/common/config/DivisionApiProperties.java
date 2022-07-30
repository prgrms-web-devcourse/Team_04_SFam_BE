package com.kdt.team04.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "division")
public record DivisionApiProperties(String key, String domain) {

}
