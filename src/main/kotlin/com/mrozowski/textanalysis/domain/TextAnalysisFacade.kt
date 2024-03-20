package com.mrozowski.textanalysis.domain

import com.mrozowski.textanalysis.domain.model.AnalysisResult
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class TextAnalysisFacade(
    private val trainerService: ModelTrainerService,
    private val textAnalysisService: TextAnalysisService
) {

    fun analyzeText(text: String): AnalysisResult {
        return textAnalysisService.analyzeText(text)
    }

    fun trainSentimentModel(inputStream: InputStream): String {
        return trainerService.trainSentimentModel(inputStream)
    }
}