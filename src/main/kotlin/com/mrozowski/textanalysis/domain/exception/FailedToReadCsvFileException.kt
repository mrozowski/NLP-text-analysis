package com.mrozowski.textanalysis.domain.exception

class FailedToReadCsvFileException(message: String, throwable: Throwable) : RuntimeException(message, throwable) {
}