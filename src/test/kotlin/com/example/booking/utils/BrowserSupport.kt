package com.example.booking.utils

import com.example.booking.config.BrowserType
import com.example.booking.config.DriverFactory
import org.openqa.selenium.WebDriver

object BrowserSupport {
    fun activeBrowsers(): List<BrowserType> = BrowserType.fromSystemProperty(System.getProperty("browser"))

    inline fun <T> withDriver(browserType: BrowserType, block: (WebDriver) -> T): T {
        val driver = DriverFactory.createDriver(browserType)
        return try {
            block(driver)
        } finally {
            try {
                driver.quit()
            } catch (_: Exception) {
                // ignore
            }
        }
    }
}
