package com.mrozowski.textanalysis.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.model")
class ModelConfigurationProperties(val sentiment: ModelProperties, val tokenizer: ModelProperties) {



    class ModelProperties(private val useDefaultModel: Boolean = true, val modelPath: String){
        fun isDefaultModel(): Boolean {
            return useDefaultModel
        }
    }
}