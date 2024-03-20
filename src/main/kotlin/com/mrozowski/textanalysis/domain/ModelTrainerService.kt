package com.mrozowski.textanalysis.domain

import com.mrozowski.textanalysis.domain.exception.FailedToReadCsvFileException
import com.mrozowski.textanalysis.infrastructure.TrainingConfigurationProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import opennlp.tools.doccat.DoccatFactory
import opennlp.tools.doccat.DocumentCategorizerME
import opennlp.tools.doccat.DocumentSample
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.util.ObjectStreamUtils
import opennlp.tools.util.TrainingParameters
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

private val csvFormat = CSVFormat.DEFAULT.builder()
    .setHeader("text", "sentiment")
    .setSkipHeaderRecord(true)
    .build()

@Service
class ModelTrainerService(
    private val modelService: NlpModelService,
    private val properties: TrainingConfigurationProperties
) {

    fun trainSentimentModel(inputStream: InputStream): String {
        logger.info { "Start training new sentiment model" }
        val csvParser = CSVParser(InputStreamReader(inputStream), csvFormat)
        val tokenizer = TokenizerME(modelService.getTokenizerModel())

        val samples = mutableListOf<DocumentSample>()
        logger.info { "Reading CSV from InputStream" }
        try {
            for (record: CSVRecord in csvParser) {
                val value = record[0]
                val category = record[1]

                val tokenizeString = tokenizer.tokenize(value)
                logger.debug { "value: ${tokenizeString.toList()}, category: $category" }
                samples.add(DocumentSample(category, tokenizeString))
            }
        } catch (exception: Exception) {
            logger.error { "Failed to read CSV input stream: $exception" }
            throw FailedToReadCsvFileException("Failed to read CSV input stream", exception)
        }

        logger.info { "Reading CSV file has been completed successfully" }
        logger.info { "Total number of read samples: ${samples.size}" }
        val objectStreams = ObjectStreamUtils.createObjectStream(samples)
        logger.info { "Training a new 'en' sentiment model" }
        val model =
            DocumentCategorizerME.train("en", objectStreams, TrainingParameters.defaultParams(), DoccatFactory())
        logger.info { "Training a new 'en' sentiment model has been completed successfully" }

        //create name
        val name = "en-sentiment-model-${generateTimestamp()}.bin"
        // save to
        val output = properties.sentiment.outputDirectory

       val path = "$output$name"
        try {
            FileOutputStream(path).use { stream ->
                model.serialize(stream)
                logger.info { "New sentiment model has been saved in $path" }
            }
        } catch (exception: IOException){
            logger.error { "Failed to save sentiment model" }
            throw exception
        }

        return path
    }

    fun generateTimestamp(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        return currentDateTime.format(formatter)
    }
}