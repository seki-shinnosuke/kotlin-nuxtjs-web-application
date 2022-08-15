package com.example.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController {

    @GetMapping("/api/v1/sample")
    fun sample(): String {
        return "[GET]sample"
    }

    @PostMapping("/api/v1/sample")
    fun postSample(): String {
        return "[POST]sample"
    }
}