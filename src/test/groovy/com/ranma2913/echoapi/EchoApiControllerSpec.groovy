package com.ranma2913.echoapi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import tools.jackson.databind.ObjectMapper

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

  def "GET returns 200 with method and url in response"() {
    when:
    def result = mockMvc.perform(get('/echo')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.method').value('GET'))
    result.andExpect(jsonPath('$.url').value('http://localhost/echo'))
  }

  def "GET returns slash as path for root path"() {
    when:
    def result = mockMvc.perform(get('/')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.path').value('/'))
  }

  def "GET returns path variable when path is present"() {
    when:
    def result = mockMvc.perform(get('/some/nested/path')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.path').value('/some/nested/path'))
  }

  def "GET returns originalUrl with query string appended"() {
    when:
    def result = mockMvc.perform(get('/echo?foo=bar&baz=qux')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.originalUrl').value('/echo?foo=bar&baz=qux'))
  }

  def "GET returns query params in response"() {
    when:
    def result = mockMvc.perform(get('/echo?foo=bar&count=3')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.query.foo').value('bar'))
    result.andExpect(jsonPath('$.query.count').value('3'))
  }

  def "GET returns empty query when no query string"() {
    when:
    def result = mockMvc.perform(get('/echo')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.query').isEmpty())
  }

  def "GET returns null body"() {
    when:
    def result = mockMvc.perform(get('/echo')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.body').doesNotExist())
  }

  def "GET returns request headers in response"() {
    when:
    def result = mockMvc.perform(get('/echo').header('x-custom-header', 'hello')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.headers.x-custom-header').value('hello'))
  }

  def "GET returns protocol, secure, host, hostname, and ip"() {
    when:
    def result = mockMvc.perform(get('/echo').header('Host', 'localhost')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.protocol').value('http'))
    result.andExpect(jsonPath('$.secure').value(false))
    result.andExpect(jsonPath('$.host').value('localhost'))
    result.andExpect(jsonPath('$.hostname').value('localhost'))
    result.andExpect(jsonPath('$.ip').exists())
  }

  def "PUT returns 200 with echoed body and method"() {
    given:
    def requestBody = [name: 'ranma2913']

    when:
    def result = mockMvc.perform(
        put('/echo')
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.method').value('PUT'))
    result.andExpect(jsonPath('$.body.name').value('ranma2913'))
  }

  def "PATCH returns 200 with echoed body and method"() {
    given:
    def requestBody = [name: 'ranma2913']

    when:
    def result = mockMvc.perform(
        patch('/echo')
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.method').value('PATCH'))
    result.andExpect(jsonPath('$.body.name').value('ranma2913'))
  }

  def "POST returns 201 with echoed body and method"() {
    given:
    def requestBody = [name: 'ranma2913']

    when:
    def result = mockMvc.perform(
        post('/echo')
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andDo(print())

    then:
    result.andExpect(status().isCreated())
    result.andExpect(jsonPath('$.method').value('POST'))
    result.andExpect(jsonPath('$.body.name').value('ranma2913'))
  }

  def "DELETE returns 200 with method and null body"() {
    when:
    def result = mockMvc.perform(delete('/echo')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.method').value('DELETE'))
    result.andExpect(jsonPath('$.body').doesNotExist())
  }

  def "GET /actuator/health returns 200 OK"() {
    when:
    def result = mockMvc.perform(get('/actuator/health')).andDo(print())

    then:
    result.andExpect(status().isOk())
  }

  def "GET /actuator/health is handled by EchoApiController when Actuator is not on classpath"() {
    when:
    def result = mockMvc.perform(get('/actuator/health')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.method').value('GET'))
    result.andExpect(jsonPath('$.path').value('/actuator/health'))
  }

  def "nested path is reflected in url and path fields"() {
    when:
    def result = mockMvc.perform(get('/foo/bar/baz')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.url').value('http://localhost/foo/bar/baz'))
    result.andExpect(jsonPath('$.path').value('/foo/bar/baz'))
    result.andExpect(jsonPath('$.originalUrl').value('/foo/bar/baz'))
  }

  def "url includes query string but path does not"() {
    when:
    def result = mockMvc.perform(get('/echo?x=1')).andDo(print())

    then:
    result.andExpect(status().isOk())
    result.andExpect(jsonPath('$.url').value('http://localhost/echo?x=1'))
    result.andExpect(jsonPath('$.path').value('/echo'))
  }
}
