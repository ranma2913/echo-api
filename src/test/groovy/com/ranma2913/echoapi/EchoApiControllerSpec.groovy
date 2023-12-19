package com.ranma2913.echoapi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class EchoApiControllerSpec extends Specification {
  @Autowired
  MockMvc mockMvc

  def "GET /echo = 200 OK"() {
    expect:
    this.mockMvc.perform(get('/echo'))
        .andDo(print())
        .andExpect(status().isOk())
  }
}
