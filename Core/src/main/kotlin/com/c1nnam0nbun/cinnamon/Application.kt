package com.c1nnam0nbun.cinnamon

class Application private constructor() {
    val world = World()

    companion object {
        operator fun invoke(init: Application.() -> Unit) {
            val app = Application()
            app.apply(init)
        }
    }
}