package com.ranma2913.echoapi


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class EchoApiApplicationSpec extends Specification {
  @Autowired(required = false)
  EchoApiApplication context

  def "Verify Context Initializes"() {
    expect:
    context
  }

  def "main() starts the application without throwing"() {
    when:
    EchoApiApplication.main(new String[0])

    then:
    noExceptionThrown()
  }

  def "getMemoryUsage() returns all four memory keys with non-null values"() {
    when:
    def usage = EchoApiApplication.MemoryUtil.getMemoryUsage()

    then:
    usage.keySet() == ["usedMemory", "freeMemory", "totalMemory", "maxMemory"] as Set
    usage.values().every { it != null && it.endsWith("m") }
  }
}
