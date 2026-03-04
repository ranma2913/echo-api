package com.ranma2913.echoapi;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = {"", "/{*path}"})
public class EchoApiController {

  private RequestDetails buildDetails(HttpServletRequest req, Object body, String path) {
    // Reconstruct full URL including query string
    var url = new StringBuilder(req.getRequestURL());
    if (StringUtils.hasText(req.getQueryString())) {
      url.append('?').append(req.getQueryString());
    }

    var headers =
        Collections.list(req.getHeaderNames()).stream()
            .collect(Collectors.toMap(h -> h, req::getHeader));

    var query =
        req.getParameterMap().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> String.join(",", e.getValue())));

    // Path variables are not available at the servlet level; use req.getAttribute for Spring's
    // parsed vars
    @SuppressWarnings("unchecked")
    var pathVars =
        (Map<String, String>)
            req.getAttribute(
                org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    var params = pathVars != null ? pathVars : Map.<String, String>of();

    log.info("Endpoint called: method={} url={}", req.getMethod(), url);

    return new RequestDetails(
        req.getMethod(),
        url.toString(),
        path != null ? path : "",
        req.getRequestURI()
            + (StringUtils.hasText(req.getQueryString()) ? "?" + req.getQueryString() : ""),
        headers,
        body,
        query,
        params,
        req.getRemoteAddr(),
        req.getScheme(),
        req.isSecure(),
        req.getHeader("host"),
        req.getServerName());
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Object get(HttpServletRequest req, @PathVariable(required = false) String path) {
    return buildDetails(req, null, path);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public Object put(
      @RequestBody Object body,
      HttpServletRequest req,
      @PathVariable(required = false) String path) {
    return buildDetails(req, body, path);
  }

  @PatchMapping
  @ResponseStatus(HttpStatus.OK)
  public Object patch(
      @RequestBody Object body,
      HttpServletRequest req,
      @PathVariable(required = false) String path) {
    return buildDetails(req, body, path);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Object post(
      @RequestBody Object body,
      HttpServletRequest req,
      @PathVariable(required = false) String path) {
    return buildDetails(req, body, path);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.OK)
  public Object delete(HttpServletRequest req, @PathVariable(required = false) String path) {
    return buildDetails(req, null, path);
  }

  record RequestDetails(
      String method,
      String url,
      String path,
      String originalUrl,
      Map<String, String> headers,
      Object body,
      Map<String, String> query,
      Map<String, String> params,
      String ip,
      String protocol,
      boolean secure,
      String host,
      String hostname) {}
}
