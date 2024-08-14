package com.c1nnam0nbun.cinnamon

class Application private constructor() {
    companion object {
        operator fun invoke(init: Application.() -> Unit) {

        }
    }
}