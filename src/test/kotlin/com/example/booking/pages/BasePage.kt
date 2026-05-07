package com.example.booking.pages

import org.openqa.selenium.By
import org.openqa.selenium.ElementClickInterceptedException
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

open class BasePage(protected val driver: WebDriver) {
    protected val wait: WebDriverWait = WebDriverWait(driver, Duration.ofSeconds(20))

    protected fun byXPath(xpath: String): By = By.xpath(xpath)

    protected fun visible(xpath: String): WebElement {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(byXPath(xpath)))
    }

    protected fun clickable(xpath: String): WebElement {
        return wait.until(ExpectedConditions.elementToBeClickable(byXPath(xpath)))
    }

    protected fun exists(xpath: String): Boolean = driver.findElements(byXPath(xpath)).isNotEmpty()

    protected fun click(xpath: String) {
        try {
            val element = clickable(xpath)

            (driver as JavascriptExecutor).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
            )

            closeBlockingOverlays()

            element.click()

        } catch (e: ElementClickInterceptedException) {

            val element = driver.findElement(By.xpath(xpath))

            (driver as JavascriptExecutor).executeScript(
                "arguments[0].click();",
                element
            )
        }
    }
    protected fun closeBlockingOverlays() {
        try {
            val overlays = driver.findElements(
                byXPath("//button[@aria-label='Закрыть календарь' or @aria-label='Close']")
            )

            if (overlays.isNotEmpty()) {
                overlays[0].click()
            }
        } catch (_: Exception) {
        }
        try {
            val overlays = driver.findElements(
                byXPath("//span[@data-testid='date-display-field-start' or normalize-space(.)='Дата заезда']")
            )

            if (overlays.isNotEmpty()) {
                overlays[0].click()
            }
        } catch (_: Exception) {
        }

        try {
            val body = driver.findElement(By.tagName("body"))
            body.sendKeys(Keys.ESCAPE)
        } catch (_: Exception) {
        }
    }

    protected fun text(xpath: String): String = visible(xpath).text.trim()

    protected fun safeClick(xpath: String): Boolean {
        val element = driver.findElements(byXPath(xpath)).firstOrNull() ?: return false
        return try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click()
            true
        } catch (_: Exception) {
            false
        }
    }

    protected fun dismissCommonPopups() {
        val xpaths = listOf(
            "//span[.//svg and .//path[contains(@d,'6.89-6.89')]]",
            "//button[@aria-label='Закрыть' or @aria-label='Close']",
            "//button[@id='onetrust-accept-btn-handler']",
            "//button[contains(.,'Принять')]",
            "//button[contains(.,'Согласен')]",
            "//button[contains(.,'Разрешить')]",
            "//button[contains(.,'OK')]"
        )

        for (xpath in xpaths) {
            try {
                safeClick(xpath)
            } catch (_: Exception) {
                // игнорим
            }
        }
        closeBlockingOverlays()
    }

    protected fun waitForUrlContains(fragment: String): Boolean {
        return wait.until(ExpectedConditions.urlContains(fragment))
    }

    protected fun firstOrNullText(xpath: String): String? {
        return try {
            driver.findElements(byXPath(xpath)).firstOrNull()?.text?.trim()?.takeIf { it.isNotBlank() }
        } catch (_: NoSuchElementException) {
            null
        }
    }
    fun firstHotelTitle(): String {
        return firstOrNullText("(//a[contains(@href, '/hotel/') and normalize-space(.)!=''])[1]")
            ?: firstOrNullText("(//h3//a[contains(@href, '/hotel/')])[1]")
            ?: ""
    }

    fun allVisibleCardsContainPhrase(vararg phrases: String): Boolean {
        return driver.findElements(By.xpath("//div[@data-testid='property-card']")).all { card ->
            val text = card.text.lowercase()
            phrases.any { phrase -> text.contains(phrase.lowercase()) }
        }
    }
    protected fun waitForCardsUpdate(previousFirstTitle: String) {
        wait.until {
            val currentFirst = firstHotelTitle()
            currentFirst.isNotBlank() && currentFirst != previousFirstTitle
        }
    }
}
