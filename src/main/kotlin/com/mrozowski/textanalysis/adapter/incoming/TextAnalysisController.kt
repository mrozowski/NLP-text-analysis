package com.mrozowski.textanalysis.adapter.incoming

import com.mrozowski.textanalysis.domain.TextAnalysisFacade
import com.mrozowski.textanalysis.domain.model.AnalysisResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("v1/analyze")
class TextAnalysisController(var facade: TextAnalysisFacade) {

    @GetMapping
    fun analyzeSocialMediaText(@RequestBody request: AnalysisRequest): ResponseEntity<AnalysisResult> {
        logger.info { "Received request for text analysis" }
        return ResponseEntity.ok(facade.analyzeText(request.text))
    }
}