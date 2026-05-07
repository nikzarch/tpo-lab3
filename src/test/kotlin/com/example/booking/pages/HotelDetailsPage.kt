package com.example.booking.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions

class HotelDetailsPage(driver: WebDriver) : BasePage(driver) {

    fun isLoaded(): Boolean {
        return try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(byXPath("//h1")))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun hotelTitle(): String = text("//h1")

    fun hasBookingControls(): Boolean {

        return driver.findElements(
            byXPath("//button | //a")
        ).any {
            val txt = it.text.lowercase()

            txt.contains("reserve") ||
                    txt.contains("book") ||
                    txt.contains("availability") ||
                    txt.contains("забронировать") ||
                    txt.contains("наличие") ||
                    txt.contains("номер")
        }
    }
}
