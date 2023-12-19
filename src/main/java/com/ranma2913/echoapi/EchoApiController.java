package com.ranma2913.echoapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/echo")
public class EchoApiController {
  @GetMapping
  @ResponseStatus(code = HttpStatus.OK)
  public void get(@RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  headers={}", headers);
  }

  @PutMapping
  @ResponseStatus(code = HttpStatus.OK)
  public void put(@RequestBody Object body, @RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  body={}\n  headers={}", body, headers);
  }

  @PatchMapping
  @ResponseStatus(code = HttpStatus.OK)
  public void patch(@RequestBody Object body, @RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  body={}\n  headers={}", body, headers);
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public void post(@RequestBody Object body, @RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  body={}\n  headers={}", body, headers);
  }

  @DeleteMapping
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void delete(@RequestBody Object body, @RequestHeader HttpHeaders headers) {
    log.info("Endpoint called with\n  body={}\n  headers={}", body, headers);
  }
}
