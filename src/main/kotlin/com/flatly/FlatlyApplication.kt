package com.flatly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlatlyApplication

fun main(args: Array<String>) {
    runApplication<FlatlyApplication>(*args)
}
