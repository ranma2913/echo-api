package com.ranma2913.echoapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.banana.BananaUtils;
import io.leego.banana.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
public class EchoApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(EchoApiApplication.class, args);
  }

  @Value("${spring.application.name}")
  String springApplicationName;

  /**
   * Print a nice welcome message when the application fully starts.
   *
   * @param event ContextRefreshedEvent means the Spring Context has fully refreshed.
   * @see <a href="https://github.com/yihleego/banana">...</a>
   */
  @SneakyThrows(JsonProcessingException.class)
  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.trace("ContextRefreshedEvent={}", event);
    log.info(" ** Application: {} Startup ** ", springApplicationName);
    var asciiArt = BananaUtils.bananaify(springApplicationName, Font.OGRE);
    var bannerBar = "*".repeat(asciiArt.split("\n")[0].length());
    log.info("\n{}\n{}\n{}", bannerBar, asciiArt, bannerBar);
    var mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
    log.info("MemoryUsage =\n{}", mapper.writeValueAsString(MemoryUtil.getMemoryUsage()));
  }

  @Slf4j
  static class MemoryUtil {
    private static final int MB = 1024 * 1024;

    public static Map<String, String> getMemoryUsage() {
      // Getting the runtime reference from system
      var runtime = Runtime.getRuntime();
      // LinkedHashMap will iterate in the order in which the entries were put into the map.
      // https://stackoverflow.com/a/2889800/1855840
      Map<String, String> properties = new LinkedHashMap<>();
      properties.put("usedMemory", ((runtime.totalMemory() - runtime.freeMemory()) / MB) + "m");
      properties.put("freeMemory", (runtime.freeMemory() / MB) + "m");
      properties.put("totalMemory", (runtime.totalMemory() / MB) + "m");
      properties.put("maxMemory", (runtime.maxMemory() / MB) + "m");
      return properties;
    }
  }
}
