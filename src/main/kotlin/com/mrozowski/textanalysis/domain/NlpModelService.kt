package com.mrozowski.textanalysis.domain

import com.mrozowski.textanalysis.domain.exception.FailedToLoadModelException
import com.mrozowski.textanalysis.infrastructure.ModelConfigurationProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import opennlp.tools.doccat.DoccatModel
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.TokenizerModel
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream

private val logger = KotlinLogging.logger {}

@Service
class NlpModelService(val properties: ModelConfigurationProperties) {

    private lateinit var sentimentModel: DoccatModel
    private lateinit var tokenizerModel: TokenizerModel
    private lateinit var personNameFinderModel: TokenNameFinderModel
    private lateinit var organizationNameFinderModel: TokenNameFinderModel

    companion object {
        private var defaultSentimentModelPath: String = "/model/default-en-sentiment-model.bin"
        private var defaultTokenizerModelPath: String = "/model/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"
        private var defaultPersonNerModelPath: String = "/model/en-ner-person.bin"
        private var defaultOrganizationNerModelPath: String = "/model/en-ner-organization.bin"
    }

    @PostConstruct
    fun loadModelsOnStart() {
        loadModels()
    }

    private fun loadModels() {
        if (properties.sentiment.isDefaultModel()) {
            loadDefaultSentimentModel()
        } else {
            loadSentimentModel()
        }

        if (properties.tokenizer.isDefaultModel()) {
            loadDefaultTokenizerModel()
        }

        loadDefaultNerModels()
    }

    private fun loadDefaultNerModels() {
        val personNerModelIn: InputStream? = javaClass.getResourceAsStream(defaultPersonNerModelPath)
        personNerModelIn?.let { model ->
            personNameFinderModel = TokenNameFinderModel(model)
            model.close()
        } ?: run {
            logger.error { "Failed to load default Person NER model. File does not exist: $defaultPersonNerModelPath" }
            throw FailedToLoadModelException("Could not find default Person NER model in $defaultPersonNerModelPath")
        }

        val organizationNerModelIn: InputStream? = javaClass.getResourceAsStream(defaultOrganizationNerModelPath)
        organizationNerModelIn?.let { model ->
            organizationNameFinderModel = TokenNameFinderModel(model)
            model.close()
        } ?: run {
            logger.error { "Failed to load default Organization NER model. File does not exist: $defaultOrganizationNerModelPath" }
            throw FailedToLoadModelException("Could not find default Organization NER model in $defaultOrganizationNerModelPath")
        }
    }

    private fun loadDefaultSentimentModel() {
        val sentimentModelIn: InputStream? = javaClass.getResourceAsStream(defaultSentimentModelPath)

        sentimentModelIn?.let { model ->
            sentimentModel = DoccatModel(model)
            model.close()
        } ?: run {
            logger.error { "Failed to load default Sentiment model. File does not exist: $defaultSentimentModelPath" }
            throw FailedToLoadModelException("Could not find default Sentiment model in $defaultSentimentModelPath")
        }
    }

    private fun loadSentimentModel() {
        val file = File(properties.sentiment.modelPath)

        if (file.exists()) {
            sentimentModel = DoccatModel(file)
        } else {
            logger.error { "Failed to load Sentiment model. File does not exist: ${properties.sentiment.modelPath}" }
            throw FailedToLoadModelException("Could not find default Sentiment model in ${properties.sentiment.modelPath}")
        }
    }

    private fun loadDefaultTokenizerModel() {
        val tokenizerModelIn: InputStream? = javaClass.getResourceAsStream(defaultTokenizerModelPath)

        tokenizerModelIn?.let { model ->
            tokenizerModel = TokenizerModel(model)
            model.close()
        } ?: run {
            logger.error { "Failed to load default Tokenizer model. File does not exist: $defaultTokenizerModelPath" }
            throw FailedToLoadModelException("Could not find default Tokenizer model in $defaultTokenizerModelPath")
        }
    }

    fun getSentimentModel(): DoccatModel {
        return sentimentModel
    }

    fun getTokenizerModel(): TokenizerModel {
        return tokenizerModel
    }

    fun getPersonNameFinderModel(): TokenNameFinderModel {
        return personNameFinderModel
    }

    fun getOrganizationNameFinderModel(): TokenNameFinderModel {
        return organizationNameFinderModel
    }
}