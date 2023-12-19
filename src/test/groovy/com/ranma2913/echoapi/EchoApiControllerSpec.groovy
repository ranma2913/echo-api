package com.ranma2913.echoapi

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class EchoApiControllerSpec extends Specification {
  @Autowired
  ObjectMapper objectMapper
  @Autowired
  MockMvc mockMvc

  @Autowired(required = false)
  EchoApiController echoApiController

  def "Verify EchoApiController Initializes"() {
    expect:
    echoApiController
  }

  def "GET /echo = 200 OK"() {
    when:
    def resultsActions = this.mockMvc.perform(get('/echo'))
        .andDo(print())

    then:
    resultsActions.andExpect(status().isOk())
    resultsActions.andExpect(jsonPath('$.name').value("ranma2913"))
  }

  def "PUT /echo = 200 OK"() {
    given:
    def requestBody = ["name": "ranma2913"]

    when:
    def resultsActions = this.mockMvc.perform(
        put('/echo')
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andDo(print())

    then:
    resultsActions.andExpect(status().isOk())
    resultsActions.andExpect(jsonPath('$.name').value("ranma2913"))
  }

  def "PATCH /echo = 200 OK"() {
    given:
    def requestBody = ["name": "ranma2913"]

    when:
    def resultsActions = this.mockMvc.perform(
        patch('/echo')
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andDo(print())

    then:
    resultsActions.andExpect(status().isOk())
    resultsActions.andExpect(jsonPath('$.name').value("ranma2913"))
  }

  def "POST /echo = 201 Created"() {
    given:
    def requestBody = ["name": "ranma2913"]

    when:
    def resultsActions = this.mockMvc.perform(
        post('/echo')
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andDo(print())

    then:
    resultsActions.andExpect(status().isCreated())
    resultsActions.andExpect(jsonPath('$.name').value("ranma2913"))
  }

  def "DELETE /echo = 204 No Content"() {
    when:
    def resultsActions = this.mockMvc.perform(delete('/echo'))
        .andDo(print())

    then:
    resultsActions.andExpect(status().isNoContent())
  }
}
