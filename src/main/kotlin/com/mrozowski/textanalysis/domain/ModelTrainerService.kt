package com.mrozowski.textanalysis.domain

import io.github.oshai.kotlinlogging.KotlinLogging
import opennlp.tools.doccat.DoccatFactory
import opennlp.tools.doccat.DoccatModel
import opennlp.tools.doccat.DocumentCategorizerME
import opennlp.tools.doccat.DocumentSample
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel
import opennlp.tools.util.ObjectStreamUtils
import opennlp.tools.util.TrainingParameters
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.InputStream
import java.io.InputStreamReader

private val logger = KotlinLogging.logger {}

@Service
class ModelTrainerService {


    fun trainSentimentModel(): DoccatModel {
        val format = CSVFormat.DEFAULT.builder()
            .setHeader("text", "sentiment")
            .setSkipHeaderRecord(true)
            .build()

        val csvParser = CSVParser(InputStreamReader(javaClass.getResourceAsStream("/model/dataset/labeled-sentiment-dataset.csv")!!), format)
        val tokenizerModel: InputStream? = javaClass.getResourceAsStream("/model/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin")
        val modelT = TokenizerModel(tokenizerModel)
        tokenizerModel?.close()

        // Initialize TokenizerME with the loaded model
        val tokenizer = TokenizerME(modelT)
        val samples = mutableListOf<DocumentSample>()
        try {

            for (record: CSVRecord in csvParser){
                val value = record[0]
                val category = record[1]

                // tokenization
                val tokenizeString = tokenizer.tokenize(value)
                logger.info{"value: ${tokenizeString.toList()}, category: $category"}
                samples.add(DocumentSample(category, tokenizeString))
            }
        } catch (exception: Exception){
            logger.error { "error: $exception" }
        }

        logger.info { "samples: ${samples.size}" }
        val objectStreams = ObjectStreamUtils.createObjectStream(samples)

        val model =
            DocumentCategorizerME.train("en", objectStreams, TrainingParameters.defaultParams(), DoccatFactory())
        return model
    }
}