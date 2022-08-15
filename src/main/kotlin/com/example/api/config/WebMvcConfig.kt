package com.example.api.config

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver
import java.nio.charset.StandardCharsets

@Configuration
@ComponentScan
class WebMvcConfig(
    private val log: Logger,
    private val mdcInterceptor: MdcInterceptor,
) : WebMvcConfigurer {
    @Value("\${allowedOrigins}")
    lateinit var allowedOrigins: String

    @Bean
    fun loggingInterceptor(): LoggingInterceptor = LoggingInterceptor(log)

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(MediaType(MediaType.APPLICATION_JSON, mapOf("charset" to "utf-8")))
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters
            .filterIsInstance<MappingJackson2HttpMessageConverter>()
            .first().defaultCharset = StandardCharsets.UTF_8
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.apply {
            addInterceptor(mdcInterceptor)
            addInterceptor(loggingInterceptor()).apply {
                addPathPatterns("/api/**")
            }
        }
    }

    /**
     * 今回のポイント①
     * サーバサイドのResources/public配下にNuxt.JSで生成した静的サイトを置くためアクセスを一律許可する
     * この設定を入れてしまうと存在しないRestAPIにアクセスした場合、無条件でhtmlリソースにアクセスされるようになる
     */
    class ResourceResolver : PathResourceResolver() {
        override fun getResource(resourcePath: String, location: Resource): Resource? {
            return super.getResource(resourcePath, location) ?: return super.getResource("index.html", location)
        }
    }
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/public/")
            .resourceChain(true)
            .addResolver(ResourceResolver())
    }

    /**
     * 今回のポイント②
     * CORS対策としてapplication.ymlに定義した特定のホストからのリクエストのみ許可する
     * Jarでフロントのリソースを同梱した際は同じホスト、同じポートからのローカルアクセスとして扱うため
     * 許可するのはローカルでの開発時にフロントをNode.jsで動作している間のlocalhost:3000のみ
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        val allowedOriginList = allowedOrigins.split(",").toTypedArray()
        registry.addMapping("/**").apply {
            allowedOrigins(*allowedOriginList)
            allowedHeaders(CorsConfiguration.ALL)
            allowedMethods(CorsConfiguration.ALL)
            allowCredentials(true)
        }
    }

    @Bean
    fun errorAttributes(): ErrorAttributes {
        return object : DefaultErrorAttributes() {
            override fun getErrorAttributes(
                webRequest: WebRequest?,
                options: ErrorAttributeOptions?
            ): MutableMap<String, Any> {
                val method = (webRequest as ServletWebRequest).request.method
                val attrs = super.getErrorAttributes(webRequest, options)
                val requestURL = attrs.getOrDefault("path", "") as String
                val ex = getError(webRequest) as Exception?
                val status = HttpStatus.valueOf(attrs["status"] as Int)
                log.warn("[$method] $requestURL $status", ex)
                return mutableMapOf(
                    "error_code" to status.name
                )
            }
        }
    }
}