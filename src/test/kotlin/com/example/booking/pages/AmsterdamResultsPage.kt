package com.example.booking.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class AmsterdamResultsPage(driver: WebDriver) : BasePage(driver) {

    companion object {
        private const val BASE_URL = "https://www.booking.com/city/nl/amsterdam.html"
        private const val MAP_FRAGMENT = "map"
    }

    fun open(): AmsterdamResultsPage {
        driver.get(BASE_URL)
        dismissCommonPopups()
        wait.until(
            ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                    byXPath(
                        "//h1[contains(.,'Amsterdam') or contains(.,'Амстердам')]"
                    )
                ),
                ExpectedConditions.titleContains("Amsterdam"),
                ExpectedConditions.titleContains("Амстердам")
            )
        )
        return this
    }

    fun isLoaded(): Boolean {
        return wait.until {
            exists("//h1[contains(normalize-space(.), 'Амстердам')]") ||
                    exists("//a[normalize-space(.)='Показать отели на карте' or .//*[normalize-space(.)='Показать отели на карте']]") ||
                    exists("//a[normalize-space(.)='Проверить наличие мест']")
        }
    }

    fun pageHeading(): String = text("//h1[contains(normalize-space(.), 'Амстердам')]")

    fun showHotelsOnMap(): AmsterdamResultsPage {
        click("//a[normalize-space(.)='Показать отели на карте' or .//*[normalize-space(.)='Показать отели на карте']]")
        waitForUrlContains(MAP_FRAGMENT)
        return this
    }

    fun sortByRatesFirst(): AmsterdamResultsPage {
        closeBlockingOverlays()
        dismissCommonPopups()
        click(
            "//button[contains(.,'отзывов')]"
        )
        wait.until {
            driver.findElements(
                By.xpath("//div[@data-testid='property-card']")
            ).isNotEmpty()
        }

        wait.until { isLoaded() }
        closeBlockingOverlays()
        dismissCommonPopups()
        return this
    }

    fun firstVisibleRates(limit: Int = 2): List<Double> {

        wait.until {
            driver.findElements(
                By.xpath("//div[@data-testid='property-card']")
            ).isNotEmpty()
        }

        val cards = driver.findElements(
            By.xpath("//div[@data-testid='property-card']//div[@data-testid='review-score']//div[contains(.,'Оценка')]")
        ).take(limit)
        return cards.map { it.text.split(" ")[1].replace(',', '.').toDouble() }
    }

    fun filterByStarRating(stars: Int): AmsterdamResultsPage {

        dismissCommonPopups()
        closeBlockingOverlays()

        val firstHotelBefore = firstHotelTitle()

        val filterXpath =
            "//label[contains(.,'$stars звезд')]"

        click(filterXpath)

        wait.until {
            val current = firstHotelTitle()
            current.isNotBlank() && current != firstHotelBefore
        }

        wait.until {
            driver.findElements(
                byXPath(
                    "(//div[@data-testid='property-card'])[1]" +
                            "//div[@role='button' and contains(@aria-label,'$stars из 5')]"
                )
            ).isNotEmpty()
        }

        return this
    }

    fun getFirstCardStarRating(): Int =
        driver.findElements(
            byXPath(
                "(//div[@data-testid='property-card'])[1]" +
                        "//div[@role='button' and contains(@aria-label,' из 5')]"
            )
        ).first().getAttribute("aria-label").split(" ")[0].toInt()


    fun firstAvailabilityLinkText(): String = text("(//a[normalize-space(.)='Проверить наличие мест'])[1]")

    fun openFirstHotel(): HotelDetailsPage {

        dismissCommonPopups()

        val oldTabs = driver.windowHandles

        click("(//a[contains(@href,'/hotel/')])[1]")

        wait.until {
            driver.windowHandles.size > oldTabs.size ||
                    driver.currentUrl.contains("/hotel/")
        }

        val newTabs = driver.windowHandles

        if (newTabs.size > oldTabs.size) {
            val newTab = newTabs.first { it !in oldTabs }
            driver.switchTo().window(newTab)
        }

        wait.until { driver.currentUrl.contains("/hotel/") }

        dismissCommonPopups()

        return HotelDetailsPage(driver)
    }

    fun firstHotelHasStarBadge(stars: Int): Boolean {
        val xpath = "(//div[@data-testid='property-card'])[1]//div[@role='img' and contains(@aria-label,'${stars}')]"
        return driver.findElements(By.xpath(xpath)).isNotEmpty()
    }

    fun searchForDestination(destination: String): AmsterdamResultsPage {
        val encoded = URLEncoder.encode(destination, StandardCharsets.UTF_8)
        driver.get("https://www.booking.com/searchresults.html&ss=$encoded")
        dismissCommonPopups()
        wait.until { exists("//a[normalize-space(.)='Проверить наличие мест']") || exists("//h1[contains(., '$destination')]") }
        return this
    }
}
