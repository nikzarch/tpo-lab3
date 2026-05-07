package com.example.booking.config

enum class BrowserType {
    CHROME,
    FIREFOX;

    companion object {
        fun fromSystemProperty(value: String?): List<BrowserType> {
            return when (value?.trim()?.lowercase()) {
                null, "", "all" -> entries.toList()
                "chrome" -> listOf(CHROME)
                "firefox" -> listOf(FIREFOX)
                else -> throw IllegalArgumentException(
                    "Unsupported browser='$value'. Use chrome, firefox, or all."
                )
            }
        }
    }
}
