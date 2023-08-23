package com.indi.product

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LauncherSpringBootApplication

fun main(args: Array<String>) {
    runApplication<LauncherSpringBootApplication>(*args)
}