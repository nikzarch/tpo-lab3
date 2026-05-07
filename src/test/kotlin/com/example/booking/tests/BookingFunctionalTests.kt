package com.example.booking.tests

import com.example.booking.config.BrowserType
import com.example.booking.pages.AmsterdamResultsPage
import com.example.booking.utils.BrowserSupport
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
class BookingFunctionalTests {

    companion object {
        @JvmStatic
        fun browsers(): Stream<Arguments> {
            return BrowserSupport.activeBrowsers().stream().map { Arguments.of(it) }
        }
    }

    @ParameterizedTest(name = "[{index}] страница результатов в {0}")
    @MethodSource("browsers")
    fun `страница результатов открывается`(browserType: BrowserType) {
        BrowserSupport.withDriver(browserType) { driver ->
            val page = AmsterdamResultsPage(driver).open()

            assertAll(
                { assertTrue(page.isLoaded(), "Страница результатов должна быть загружена") },
                { assertFalse(page.pageHeading().isBlank(), "Заголовок страницы не должен быть пустым") },
                { assertTrue(page.pageHeading().contains("Амстердам", ignoreCase = true), "Заголовок должен содержать 'Амстердам'") },
                { assertFalse(page.firstAvailabilityLinkText().isBlank(), "Должна быть видна хотя бы одна ссылка на отель") }
            )
        }
    }

    @ParameterizedTest(name = "[{index}] открытие отеля в {0}")
    @MethodSource("browsers")
    fun `пользователь может открыть первую карточку отеля`(browserType: BrowserType) {
        BrowserSupport.withDriver(browserType) { driver ->
            val details = AmsterdamResultsPage(driver).open().openFirstHotel()

            assertAll(
                { assertTrue(details.isLoaded(), "Страница отеля должна быть загружена") },
                { assertFalse(details.hotelTitle().isBlank(), "Название отеля не должно быть пустым") },
                { assertTrue(details.hasBookingControls(), "На странице должны быть видимы элементы бронирования") }
            )
        }
    }

    @ParameterizedTest(name = "[{index}] карта в {0}")
    @MethodSource("browsers")
    fun `пользователь может открыть карту`(browserType: BrowserType) {
        BrowserSupport.withDriver(browserType) { driver ->
            val page = AmsterdamResultsPage(driver).open().showHotelsOnMap()

            assertAll(
                { assertTrue(driver.currentUrl.contains("map", ignoreCase = true), "После открытия карты URL должен содержать 'map'") },
                { assertFalse(page.pageHeading().isBlank(), "После перехода на карту заголовок не должен быть пустым") }
            )
        }
    }

    @ParameterizedTest(name = "[{index}] сортировка в {0}")
    @MethodSource("browsers")
    fun `пользователь может использовать сортировку`(
        browserType: BrowserType
    ) {

        BrowserSupport.withDriver(browserType) { driver ->

            val page = AmsterdamResultsPage(driver).open()

            page.sortByRatesFirst()

            val rates = page.firstVisibleRates(2)

            assertAll(

                {
                    assertTrue(
                        page.isLoaded(),
                        "После сортировки страница должна оставаться загруженной"
                    )
                },

                {
                    assertTrue(
                        rates.size >= 2,
                        "Должны быть найдены оценки минимум двух отелей"
                    )
                },

                {
                    assertTrue(
                        rates[0] >= rates[1],
                        "Оценка первого отеля должна быть круче оценки второго"
                    )
                }
            )
        }
    }

    @ParameterizedTest(name = "[{index}] фильтр звезд в {0}")
    @MethodSource("browsers")
    fun `пользователь может применить фильтр звезд и он работает`(browserType: BrowserType) {
        BrowserSupport.withDriver(browserType) { driver ->
            for (stars in 1..5){
                val page = AmsterdamResultsPage(driver).open()
                page.filterByStarRating(stars)

                assertAll(
                    { assertTrue(page.isLoaded(), "После применения фильтра страница должна оставаться загруженной") },
                    { assertFalse(page.firstHotelTitle().isBlank(), "После фильтра должен быть виден хотя бы один отель") },
                    { assertEquals(page.getFirstCardStarRating(),stars)}
                )
            }


        }
    }
}