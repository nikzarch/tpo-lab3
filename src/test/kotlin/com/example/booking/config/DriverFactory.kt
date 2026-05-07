package com.example.booking.config

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

object DriverFactory {

    fun createDriver(browser: BrowserType): WebDriver {
        return when (browser) {

            BrowserType.CHROME -> {
                WebDriverManager.chromedriver().setup()
                ChromeDriver()
            }

            BrowserType.FIREFOX -> {
                WebDriverManager.firefoxdriver().setup()

                val options = FirefoxOptions()
                options.addArguments("--width=1920")
                options.addArguments("--height=1080")

                FirefoxDriver(options)
            }
        }
    }
}