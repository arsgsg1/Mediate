package com.ko.mediate.HC.common;

import static java.util.stream.Collectors.toList;

import com.ko.mediate.HC.common.exception.MediateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Path.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.ko.mediate.HC.common.ErrorResponseBuilder.*;

@RestControllerAdvice
@Slf4j
public class GlobalApiExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(value = {AuthenticationException.class, BadCredentialsException.class})
  public ResponseEntity<Object> handleAuthenticationException(
      final AuthenticationException ex, final ServletWebRequest request) {
    log(ex, request);
    final ErrorResponseDto errorResponseDto =
        build(
            AuthenticationException.class.getSimpleName(),
            ex.getMessage(),
            HttpStatus.UNAUTHORIZED);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDto);
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> handleUncaughtException(
      final Exception ex, final ServletWebRequest request) {
    log(ex, request);
    final ErrorResponseDto errorResponseDto =
        build(
            Exception.class.getSimpleName(),
            "????????? ????????? ????????? ????????? ??????????????????.",
            HttpStatus.INTERNAL_SERVER_ERROR);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
  }

  @ExceptionHandler({MediateException.class})
  public ResponseEntity<Object> handleCustomUncaughtDomainLayerException(
      final MediateException ex, final ServletWebRequest request) {
    log(ex, request);
    final ErrorResponseDto errorResponseDto =
        build(ex.getErrorCode().getCode(), ex.getMessage(), ex.status());
    return ResponseEntity.status(ex.status()).body(errorResponseDto);
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(
      final ConstraintViolationException ex, final ServletWebRequest request) {
    log(ex, request);

    final List<InvalidParameterDto> invalidParameters = new ArrayList<>();
    ex.getConstraintViolations()
        .forEach(
            constraintViolation -> {
              final Iterator<Node> it = constraintViolation.getPropertyPath().iterator();
              if (it.hasNext()) {
                try {
                  it.next();
                  final Path.Node n = it.next();
                  final InvalidParameterDto invalidParameter = new InvalidParameterDto();
                  invalidParameter.setParameter(n.getName());
                  invalidParameter.setMessage(constraintViolation.getMessage());
                  invalidParameters.add(invalidParameter);
                } catch (final Exception e) {
                  log.warn("Can't extract the information about constraint violation");
                }
              }
            });

    final ErrorResponseDto errorResponseDto =
        build(
            ConstraintViolationException.class.getSimpleName(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            HttpStatus.BAD_REQUEST,
            invalidParameters);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
  }

  // ????????? ????????? ????????? ????????? ???????????? ??????
  @ExceptionHandler({MaxUploadSizeExceededException.class})
  public ResponseEntity<Object> handleMaxUploadSizeExceeded(
      final MaxUploadSizeExceededException ex, final ServletWebRequest request) {
    final ErrorResponseDto errorResponseDto =
        build(
            ex.getClass().getSimpleName(),
            HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(),
            HttpStatus.PAYLOAD_TOO_LARGE);
    log(ex, request);
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponseDto);
  }

  // ????????? ??????????????? ????????? ??? ?????? ??????
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log(ex, (ServletWebRequest) request);
    final ErrorResponseDto errorResponseDto =
        build(
            HttpMessageNotReadableException.class.getSimpleName(),
            ex.getMessage(),
            HttpStatus.BAD_REQUEST);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      final HttpRequestMethodNotSupportedException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log(ex, (ServletWebRequest) request);
    final ErrorResponseDto errorResponseDto =
        build(
            HttpRequestMethodNotSupportedException.class.getSimpleName(),
            ex.getMessage(),
            HttpStatus.METHOD_NOT_ALLOWED);
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponseDto);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log(ex, (ServletWebRequest) request);
    final List<InvalidParameterDto> invalidParameters =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    InvalidParameterDto.builder()
                        .parameter(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
            .collect(toList());

    final ErrorResponseDto errorResponseDto =
        build(
            MethodArgumentNotValidException.class.getSimpleName(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            HttpStatus.BAD_REQUEST,
            invalidParameters);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      final MissingServletRequestParameterException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log(ex, (ServletWebRequest) request);

    final ErrorResponseDto errorResponseDto =
        build(
            MissingServletRequestParameterException.class.getSimpleName(),
            String.format("%s: %s", HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage()),
            HttpStatus.BAD_REQUEST);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
  }

  private void log(final Exception ex, final ServletWebRequest request) {
    final Optional<HttpMethod> httpMethod;
    final Optional<String> requestUrl;

    final Optional<ServletWebRequest> possibleIncomingNullRequest = Optional.ofNullable(request);
    if (possibleIncomingNullRequest.isPresent()) {
      // get the HTTP Method
      httpMethod = Optional.ofNullable(possibleIncomingNullRequest.get().getHttpMethod());
      if (Optional.ofNullable(possibleIncomingNullRequest.get().getRequest()).isPresent()) {
        // get the Request URL
        requestUrl =
            Optional.of(possibleIncomingNullRequest.get().getRequest().getRequestURL().toString());
      } else {
        requestUrl = Optional.empty();
      }
    } else {
      httpMethod = Optional.empty();
      requestUrl = Optional.empty();
    }

    log.error(
        "Request {} {} failed with exception reason: {}",
        (httpMethod.isPresent() ? httpMethod.get() : "'null'"),
        (requestUrl.orElse("'null'")),
        ex.getMessage(),
        ex);
  }
}
