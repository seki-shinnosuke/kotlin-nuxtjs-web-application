package com.example.api.config

import org.slf4j.Logger
import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ErrorHandler(
    private val log: Logger,
    private val req: HttpServletRequest
) : ResponseEntityExceptionHandler() {
    /**
     * ResponseStatusExceptionハンドリング
     */
    @ExceptionHandler(ResponseStatusException::class)
    private fun handleResponseStatusException(
        ex: ResponseStatusException
    ): ResponseEntity<Map<String, Any?>> {
        val message = ("[${req.method}] ${req.requestURI} ${ex.status}")
        when (getLogLevel(ex.status)) {
            LogLevel.INFO -> log.info(message, ex)
            LogLevel.ERROR -> log.error(message, ex)
            else -> log.error(message, ex)
        }
        return ResponseEntity(mapOf("error_code" to ex.status.name), ex.status)
    }

    /**
     * 未ハンドング例外のハンドリング
     */
    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val message = ("[${req.method}] ${req.requestURI} $status")
        when (getLogLevel(status)) {
            LogLevel.INFO -> log.info(message, ex)
            LogLevel.ERROR -> log.error(message, ex)
            else -> log.error(message, ex)
        }
        return ResponseEntity(mapOf("error_code" to status.name), status)
    }

    /**
     * HttpStatusよりLogLevel取得
     */
    private fun getLogLevel(status: HttpStatus): LogLevel {
        return when {
            status.is5xxServerError && status != HttpStatus.SERVICE_UNAVAILABLE -> LogLevel.ERROR
            else -> LogLevel.INFO
        }
    }
}