package com.mrozowski.textanalysis.domain.model

data class AnalysisResult(
    val sentiment: Sentiment,
    val namedEntity: NamedEntity
)

data class NamedEntity(val personNames: List<String>, val organizationNames: List<String>)

enum class Sentiment {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
}