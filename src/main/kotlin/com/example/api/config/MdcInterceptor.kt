package com.example.api.config

import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.AsyncHandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class MdcInterceptor : AsyncHandlerInterceptor {
    companion object {
        private const val IP_ADDRESS = "ipAddress"
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        MDC.remove(IP_ADDRESS)
        MDC.put(IP_ADDRESS, getIpAddress(request))
        return true
    }

    override fun afterConcurrentHandlingStarted(request: HttpServletRequest, response: HttpServletResponse, handler: Any) {
        MDC.clear()
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        MDC.clear()
    }

    private fun getIpAddress(request: HttpServletRequest): String {
        return try {
            request.getHeader("X-Forwarded-For")?.let {
                if (it.contains(",")) {
                    it.split(",")[0].trim()
                } else {
                    it.trim()
                }
            } ?: request.remoteAddr
        } catch (e: Throwable) {
            ""
        }
    }
}