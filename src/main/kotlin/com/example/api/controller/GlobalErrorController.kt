package com.example.api.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

/**
 * 本アプリケーションはSpringBoot同梱のTomcatを利用して動かすため
 * 不正なリクエストが発生した際にTomcatのデフォルトエラー画面を表示してしまう可能性がある
 * アーキテクチャはなるべくOpenにしないほうが良いためHttpStatus 404を返すようにカスタマイズする
 */
@Controller
class GlobalErrorController : ErrorController {
    companion object {
        const val ERROR_PATH = "/error"
    }

    @RequestMapping(value = [ERROR_PATH])
    fun error(request: HttpServletRequest): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
}