package com.mrozowski.textanalysis.infrastructure

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ModelConfigurationProperties::class)
class DefaultModelConfiguration {


}