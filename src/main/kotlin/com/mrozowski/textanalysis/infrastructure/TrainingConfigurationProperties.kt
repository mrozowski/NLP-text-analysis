package com.mrozowski.textanalysis.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.training")
class TrainingConfigurationProperties(val sentiment: TrainingModelProperties) {


    class TrainingModelProperties(val outputDirectory: String){
    }
}