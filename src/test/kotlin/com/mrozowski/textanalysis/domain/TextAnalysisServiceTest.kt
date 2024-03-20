package com.mrozowski.textanalysis.domain

import com.mrozowski.textanalysis.domain.model.Sentiment
import opennlp.tools.doccat.DoccatModel
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.TokenizerModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.io.InputStream

private var defaultSentimentModelPath: String = "/model/default-en-sentiment-model.bin"
private var defaultTokenizerModelPath: String = "/model/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"
private var defaultPersonNerModelPath: String = "/model/en-ner-person.bin"
private var defaultOrganizationNerModelPath: String = "/model/en-ner-organization.bin"

@ExtendWith(MockitoExtension::class)
class TextAnalysisServiceTest {

    @Mock
    private lateinit var modelService: NlpModelService

    @InjectMocks
    private lateinit var underTest: TextAnalysisService

    @Test
    fun shouldAnalyzeText() {
        val text = "Tom, my car broke and now I am late to work"
        val semanticModel = loadModel(defaultSentimentModelPath, ::DoccatModel)
        val tokenizerModel = loadModel(defaultTokenizerModelPath, ::TokenizerModel)
        val personNerModel = loadModel(defaultPersonNerModelPath, ::TokenNameFinderModel)
        val organizationNerModel = loadModel(defaultOrganizationNerModelPath, ::TokenNameFinderModel)

        Mockito.`when`(modelService.getSentimentModel()).thenReturn(semanticModel)
        Mockito.`when`(modelService.getTokenizerModel()).thenReturn(tokenizerModel)
        Mockito.`when`(modelService.getPersonNameFinderModel()).thenReturn(personNerModel)
        Mockito.`when`(modelService.getOrganizationNameFinderModel()).thenReturn(organizationNerModel)


        val analyzeText = underTest.analyzeText(text)
        print(analyzeText)
        assert(analyzeText.sentiment == Sentiment.NEGATIVE)
        assert(analyzeText.namedEntity.personNames == listOf("Tom"))
        assert(analyzeText.namedEntity.organizationNames == emptyList<String>())
    }


    private fun <T> loadModel(path: String, constructor: (InputStream) -> T): T{
        return constructor(javaClass.getResourceAsStream(path)!!)
    }
}