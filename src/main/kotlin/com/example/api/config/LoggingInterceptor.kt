package com.example.api.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.InjectionPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class LoggerInjectionConfig {
    @Bean
    @Scope("prototype")
    fun logger(ip: InjectionPoint): Logger {
        return LoggerFactory.getLogger(
            ip.methodParameter?.containingClass
                ?: ip.field?.declaringClass
                ?: throw BeanCreationException("Failed Logger Injection.")
        )
    }
}

class LoggingInterceptor(
    private val log: Logger
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // api配下へのリクエストは全てリクエストログ出力
        log.info("REQUEST - {} {}", request.method, request.requestURI)
        return super.preHandle(request, response, handler)
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        // 正常終了の場合、レスポンスログ出力
        if (handler is HandlerMethod) {
            log.info("RESPONSE - {} {} {}", request.method, request.requestURI, response.status.toString())
        }
        super.postHandle(request, response, handler, modelAndView)
    }
}