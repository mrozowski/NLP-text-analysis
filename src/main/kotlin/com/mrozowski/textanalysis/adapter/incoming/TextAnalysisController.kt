package com.mrozowski.textanalysis.adapter.incoming

import com.mrozowski.textanalysis.domain.TextAnalysisService
import com.mrozowski.textanalysis.domain.model.AnalysisResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("v1/rest")
class TextAnalysisController(var analysisService: TextAnalysisService) {

    @PostMapping("/analyze")
    fun analyzeSocialMediaText(@RequestBody request: AnalysisRequest): ResponseEntity<AnalysisResult> {
        logger.info { "Received request for text analysis" }
        return ResponseEntity.ok(analysisService.analyzeText(request.text))
    }
}