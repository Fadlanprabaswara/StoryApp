package com.example.storyapp.model

open class properti<out T>(private val content: T) {

    @Suppress("Private")
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) null else content.also { hasBeenHandled = true }
}