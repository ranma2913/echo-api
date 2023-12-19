package com.ranma2913.echoapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/echo")
public class EchoApiController {
  String defaultResponse = """
{"name": "ranma2913"}
""";

  @GetMapping
  @ResponseStatus(code = HttpStatus.OK)
  public Object get(@RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  headers={}", headers);
    return defaultResponse;
  }

  @PutMapping
  @ResponseStatus(code = HttpStatus.OK)
  public Object put(@RequestBody Object body, @RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  body={}\n  headers={}", body, headers);
    return body;
  }

  @PatchMapping
  @ResponseStatus(code = HttpStatus.OK)
  public Object patch(@RequestBody Object body, @RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  body={}\n  headers={}", body, headers);
    return body;
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public Object post(@RequestBody Object body, @RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  body={}\n  headers={}", body, headers);
    return body;
  }

  @DeleteMapping
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void delete(@RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  headers={}", headers);
  }
}
