package com.mrozowski.textanalysis.adapter.incoming

import com.mrozowski.textanalysis.domain.TextAnalysisService
import com.mrozowski.textanalysis.domain.model.AnalysisResult
import com.mrozowski.textanalysis.domain.model.NamedEntity
import com.mrozowski.textanalysis.domain.model.Sentiment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class TextAnalysisControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var analysisService: TextAnalysisService

    @InjectMocks
    private lateinit var controller: TextAnalysisController

    @Test
    fun shouldAnalyzeSocialMediaText() {
        val text = "Test text to analyze"
        val request = AnalysisRequest(text)
        val analysisResult = AnalysisResult(Sentiment.NEUTRAL, NamedEntity(emptyList(), emptyList()))

        Mockito.`when`(analysisService.analyzeText(request.text)).thenReturn(analysisResult)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()


        mockMvc.perform(
            post("/v1/rest/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\": \"$text\"}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.sentiment").value("NEUTRAL"))
            .andExpect(jsonPath("$.namedEntity.personNames").isEmpty)
            .andExpect(jsonPath("$.namedEntity.organizationNames").isEmpty)
    }
}