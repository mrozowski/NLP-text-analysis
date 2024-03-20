package com.mrozowski.textanalysis.adapter.incoming

import com.mrozowski.textanalysis.domain.TextAnalysisFacade
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("v1/model")
class ModelController(var facade: TextAnalysisFacade) {

    @PostMapping("/train-sentiment-model")
    fun trainSentimentModel(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        logger.info { "Received request to train sentiment model" }
        return ResponseEntity.ok(facade.trainSentimentModel(file.inputStream))
    }
}