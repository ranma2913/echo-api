package com.ranma2913.echoapi


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = EchoApiApplication)
class EchoApiApplicationSpec extends Specification {
  @Autowired(required = false)
  EchoApiApplication context

  def "Verify Context Loads"() {
    context
  }
}
