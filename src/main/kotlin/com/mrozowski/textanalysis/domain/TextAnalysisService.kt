package com.mrozowski.textanalysis.domain

import com.mrozowski.textanalysis.domain.model.AnalysisResult
import com.mrozowski.textanalysis.domain.model.NamedEntity
import com.mrozowski.textanalysis.domain.model.Sentiment
import io.github.oshai.kotlinlogging.KotlinLogging
import opennlp.tools.doccat.DocumentCategorizerME
import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.util.Span
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class TextAnalysisService(private val modelService: NlpModelService) {

    fun analyzeText(text: String): AnalysisResult {
        logger.info { "Start text analysis: $text " }
        val tokenizer = TokenizerME(modelService.getTokenizerModel())
        val tokenizeText = tokenizer.tokenize(text)

        val sentiment = analyzeSentiment(tokenizeText)
        val namedEntity = detectNamedEntities(tokenizeText)

        return AnalysisResult(sentiment, namedEntity)
    }

    private fun analyzeSentiment(input: Array<String>): Sentiment {
        logger.info { "Analyzing sentiment" }

        val analyzer = DocumentCategorizerME(modelService.getSentimentModel())

        val outcomes = analyzer.categorize(input)
        val category = analyzer.getBestCategory(outcomes)

        return when (category) {
            "positive" -> Sentiment.POSITIVE
            "negative" -> Sentiment.NEGATIVE
            else -> Sentiment.NEUTRAL
        }
    }

    private fun detectNamedEntities(input: Array<String>): NamedEntity {
        logger.info { "Detecting name entity" }
        val personNameFinder = NameFinderME(modelService.getPersonNameFinderModel())
        val personSpans = personNameFinder.find(input)
        val personNames = Span.spansToStrings(personSpans, input).toList()

        val organizationNameFinder = NameFinderME(modelService.getOrganizationNameFinderModel())
        val organizationSpan = organizationNameFinder.find(input)
        val organizationNames = Span.spansToStrings(organizationSpan, input).toList()

        return NamedEntity(personNames, organizationNames)
    }

}