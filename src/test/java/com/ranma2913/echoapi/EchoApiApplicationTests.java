package com.ranma2913.echoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class EchoApiApplicationTests {
  @Autowired(required = false)
  EchoApiApplication context;

  @Test
  void contextLoads() {
    Assert.notNull(context, "Context should not be null");
  }
}
