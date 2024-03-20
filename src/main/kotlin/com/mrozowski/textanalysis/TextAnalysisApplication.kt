package com.mrozowski.textanalysis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TextAnalysisApplication

fun main(args: Array<String>) {
    runApplication<TextAnalysisApplication>(*args)
}
