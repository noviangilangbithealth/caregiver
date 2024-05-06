package com.siloamhospitals.siloamcaregiver.ext.datetime

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

typealias LocalTimeRange = Pair<LocalTime, LocalTime>
typealias LocalDateRange = Pair<LocalDate, LocalDate>
typealias LocalDateTimeRange = Pair<LocalDateTime, LocalDateTime>

internal inline val LOCALE_IND: Locale
    get() = Locale("in", "ID")

inline val THIS_TIME: LocalTime
    get() = LocalTime.now()
inline val THIS_HOUR: Int
    get() = THIS_TIME.hour
inline val THIS_MINUTE: Int
    get() = THIS_TIME.minute
inline val THIS_SECOND: Int
    get() = THIS_TIME.second
inline val THIS_MILLI_SECOND: Int
    get() = THIS_TIME.nano
@Suppress("MagicNumber")
inline val END_OF_DAY: LocalTime
    get() = LocalTime.MIDNIGHT.minusNanos(1)

inline val TODAY: LocalDate
    get() = LocalDate.now()
inline val TOMORROW: LocalDate
    get() = TODAY.plusDays(1)
inline val YESTERDAY: LocalDate
    get() = TODAY.minusDays(1)
inline val NEXT_WEEK: LocalDate
    get() = TODAY.plusWeeks(1)
inline val LAST_WEEK: LocalDate
    get() = TODAY.minusWeeks(1)
inline val NEXT_MONTH: LocalDate
    get() = TODAY.plusMonths(1)
inline val LAST_MONTH: LocalDate
    get() = TODAY.minusMonths(1)
inline val NEXT_YEAR: LocalDate
    get() = TODAY.plusYears(1)
inline val LAST_YEAR: LocalDate
    get() = TODAY.minusYears(1)

inline val NOW: LocalDateTime
    get() = LocalDateTime.now()

const val ONE_SECOND_IN_MILLIS = 1_000L

const val ONE_MINUTE_IN_SECONDS = 60
const val ONE_MINUTE_IN_MILLIS = ONE_MINUTE_IN_SECONDS * ONE_SECOND_IN_MILLIS

const val ONE_HOUR_IN_MINUTES = 60
const val ONE_HOUR_IN_SECONDS = ONE_HOUR_IN_MINUTES * ONE_MINUTE_IN_SECONDS
const val ONE_HOUR_IN_MILLIS = ONE_HOUR_IN_MINUTES * ONE_MINUTE_IN_MILLIS

const val ONE_DAY_IN_HOURS = 24
const val ONE_DAY_IN_MINUTES = ONE_DAY_IN_HOURS * ONE_HOUR_IN_MINUTES
const val ONE_DAY_IN_SECONDS = ONE_DAY_IN_MINUTES * ONE_MINUTE_IN_SECONDS
const val ONE_DAY_IN_MILLIS = ONE_DAY_IN_SECONDS * ONE_SECOND_IN_MILLIS

const val ONE_WEEK_IN_DAYS = 7
const val ONE_WEEK_IN_HOURS = ONE_WEEK_IN_DAYS * ONE_DAY_IN_HOURS
const val ONE_WEEK_IN_MINUTES = ONE_WEEK_IN_HOURS * ONE_HOUR_IN_MINUTES
const val ONE_WEEK_IN_SECONDS = ONE_WEEK_IN_MINUTES * ONE_MINUTE_IN_SECONDS
const val ONE_WEEK_IN_MILLIS = ONE_WEEK_IN_SECONDS * ONE_SECOND_IN_MILLIS

const val MONTHS_OF_YEAR = 12
const val DAYS_OF_WEEK = 7

internal inline val String.asFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern(this)

internal inline val FormatStyle.asFormatter: DateTimeFormatter
    get() = DateTimeFormatter.ofLocalizedDateTime(this)

//region Parsing
//region Timestamp

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

infix fun Long.toLocalDateTime(zoneId: ZoneId): LocalDateTime =
    Instant.ofEpochMilli(this)
        .atZone(zoneId)
        .toLocalDateTime()

fun Long.toLocalDateTimeOrNow(): LocalDateTime =
    toLocalDateTimeOrDefault(NOW)

infix fun Long.toLocalDateTimeOrNow(zoneId: ZoneId): LocalDateTime =
    toLocalDateTimeOrDefault(zoneId, NOW)

infix fun Long.toLocalDateTimeOrDefault(dateTime: LocalDateTime): LocalDateTime =
    runCatching {
        Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }.getOrDefault(dateTime)

fun Long.toLocalDateTimeOrDefault(zoneId: ZoneId, dateTime: LocalDateTime): LocalDateTime =
    runCatching {
        Instant.ofEpochMilli(this)
            .atZone(zoneId)
            .toLocalDateTime()
    }.getOrDefault(dateTime)

//endregion
//region Date Time String

fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this)

fun String.toLocalDateTimeOrNow(): LocalDateTime =
    toLocalDateTimeOrDefault(NOW)

infix fun String.toLocalDateTimeOrDefault(dateTime: LocalDateTime): LocalDateTime =
    runCatching { LocalDateTime.parse(this) }.getOrDefault(dateTime)

infix fun String.toLocalDateTime(pattern: String): LocalDateTime =
    LocalDateTime.parse(this, pattern.asFormatter)

infix fun String.toLocalDateTimeOrNow(pattern: String): LocalDateTime =
    toLocalDateTimeOrDefault(pattern, NOW)

fun String.toLocalDateTimeOrDefault(pattern: String, dateTime: LocalDateTime): LocalDateTime =
    runCatching { LocalDateTime.parse(this, pattern.asFormatter) }.getOrDefault(dateTime)

fun String.toLocalDateTime(pattern: String, locale: Locale): LocalDateTime =
    LocalDateTime.parse(this, pattern.asFormatter.withLocale(locale))

fun String.toLocalDateTimeOrNow(pattern: String, locale: Locale): LocalDateTime =
    toLocalDateTimeOrDefault(pattern, locale, NOW)

fun String.toLocalDateTimeOrDefault(pattern: String, locale: Locale, dateTime: LocalDateTime): LocalDateTime =
    runCatching { LocalDateTime.parse(this, pattern.asFormatter.withLocale(locale)) }.getOrDefault(dateTime)

infix fun String.toLocalDateTime(style: FormatStyle): LocalDateTime =
    LocalDateTime.parse(this, style.asFormatter)

infix fun String.toLocalDateTimeOrNow(style: FormatStyle): LocalDateTime =
    toLocalDateTimeOrDefault(style, NOW)

fun String.toLocalDateTimeOrDefault(style: FormatStyle, dateTime: LocalDateTime): LocalDateTime =
    runCatching { LocalDateTime.parse(this, style.asFormatter) }.getOrDefault(dateTime)

fun String.toLocalDateTime(style: FormatStyle, locale: Locale): LocalDateTime =
    LocalDateTime.parse(this, style.asFormatter.withLocale(locale))

fun String.toLocalDateTimeOrNow(style: FormatStyle, locale: Locale): LocalDateTime =
    toLocalDateTimeOrDefault(style, locale, NOW)

fun String.toLocalDateTimeOrDefault(style: FormatStyle, locale: Locale, dateTime: LocalDateTime): LocalDateTime =
    runCatching { LocalDateTime.parse(this, style.asFormatter.withLocale(locale)) }.getOrDefault(dateTime)

//endregion
//endregion
//region Formatting

infix fun LocalDateTime.withIndFormat(pattern: String): String =
    withFormat(pattern, LOCALE_IND)

infix fun LocalDateTime.withIndFormat(style: FormatStyle): String =
    withFormat(style, LOCALE_IND)

infix fun LocalDateTime.withFormat(pattern: String): String =
    withFormat(pattern, Locale.getDefault())

fun LocalDateTime.withFormat(pattern: String, locale: Locale = Locale.getDefault()): String =
    format(DateTimeFormatter.ofPattern(pattern, locale))

infix fun LocalDateTime.withFormat(style: FormatStyle): String =
    withFormat(style, Locale.getDefault())

fun LocalDateTime.withFormat(style: FormatStyle, locale: Locale = Locale.getDefault()): String =
    format(DateTimeFormatter.ofLocalizedDateTime(style).withLocale(locale))

//endregion
//region Manipulation

inline val LocalDateTime.toStartOfTheDay: LocalDateTime
    get() = toLocalDate().atStartOfDay()

inline val LocalDateTime.toEndOfTheDay: LocalDateTime
    get() = LocalDateTime.of(toLocalDate(), END_OF_DAY)

//endregion
//region Conversion

fun LocalDateTime.sinceStartOfTheDay(): LocalDateTimeRange =
    toStartOfTheDay to this

fun LocalDateTime.untilEndOfTheDay(): LocalDateTimeRange =
    this to toEndOfTheDay

fun LocalDateTime.sinceYesterday(): LocalDateTimeRange =
    minusDays(1) to this

fun LocalDateTime.sincePreviousWeek(): LocalDateTimeRange =
    this to minusDays(DAYS_OF_WEEK.toLong())

fun LocalDateTime.sincePreviousMonth(): LocalDateTimeRange =
    this to minusMonths(1)

fun LocalDateTime.sincePreviousYear(): LocalDateTimeRange =
    this to minusYears(1)

fun LocalDateTime.untilTomorrow(): LocalDateTimeRange =
    this to plusDays(1)

fun LocalDateTime.untilNextWeek(): LocalDateTimeRange =
    this to plusDays(DAYS_OF_WEEK.toLong())

fun LocalDateTime.untilNextMonth(): LocalDateTimeRange =
    this to plusMonths(1)

fun LocalDateTime.untilNextYear(): LocalDateTimeRange =
    this to plusYears(1)

//endregion
//region Checking

inline val LocalDateTime.isNow: Boolean
    get() = equals(NOW)

inline val LocalDateTime.isPast: Boolean
    get() = isBefore(LocalDateTime.now())

inline val LocalDateTime.isFuture: Boolean
    get() = isAfter(LocalDateTime.now())

infix fun LocalDateTime.isWithin(dateTimeRange: LocalDateTimeRange): Boolean =
    isEqual(dateTimeRange.first) || isEqual(dateTimeRange.second) || isWithinExclusive(dateTimeRange)

infix fun LocalDateTime.isWithinExclusive(dateTimeRange: LocalDateTimeRange): Boolean =
    isBefore(dateTimeRange.second) && isAfter(dateTimeRange.first)

//endregion

//region Parsing

fun String.toLocalDate(): LocalDate =
    LocalDate.parse(this)

fun String.toLocalDateOrToday(): LocalDate =
    toLocalDateOrDefault(TODAY)

infix fun String.toLocalDateOrDefault(localDate: LocalDate): LocalDate =
    runCatching { LocalDate.parse(this) }.getOrDefault(localDate)

infix fun String.toLocalDate(pattern: String): LocalDate =
    LocalDate.parse(this, pattern.asFormatter)

infix fun String.toLocalDateOrToday(pattern: String): LocalDate =
    toLocalDateOrDefault(pattern, TODAY)

fun String.toLocalDateOrDefault(pattern: String, localDate: LocalDate): LocalDate =
    runCatching { LocalDate.parse(this, pattern.asFormatter) }.getOrDefault(localDate)

fun String.toLocalDate(pattern: String, locale: Locale): LocalDate =
    LocalDate.parse(this, pattern.asFormatter.withLocale(locale))

fun String.toLocalDateOrToday(pattern: String, locale: Locale): LocalDate =
    toLocalDateOrDefault(pattern, locale, TODAY)

fun String.toLocalDateOrDefault(pattern: String, locale: Locale, localDate: LocalDate): LocalDate =
    runCatching { LocalDate.parse(this, pattern.asFormatter.withLocale(locale)) }.getOrDefault(localDate)

infix fun String.toLocalDate(style: FormatStyle): LocalDate =
    LocalDate.parse(this, style.asFormatter)

infix fun String.toLocalDateOrToday(style: FormatStyle): LocalDate =
    toLocalDateOrDefault(style, TODAY)

fun String.toLocalDateOrDefault(style: FormatStyle, localDate: LocalDate): LocalDate =
    runCatching { LocalDate.parse(this, style.asFormatter) }.getOrDefault(localDate)

fun String.toLocalDate(style: FormatStyle, locale: Locale): LocalDate =
    LocalDate.parse(this, style.asFormatter.withLocale(locale))

fun String.toLocalDateOrToday(style: FormatStyle, locale: Locale): LocalDate =
    toLocalDateOrDefault(style, locale, TODAY)

fun String.toLocalDateOrDefault(style: FormatStyle, locale: Locale, localDate: LocalDate): LocalDate =
    runCatching { LocalDate.parse(this, style.asFormatter.withLocale(locale)) }.getOrDefault(localDate)

//endregion
//region Formatting

infix fun LocalDate.withIndFormat(pattern: String): String =
    withFormat(pattern, LOCALE_IND)

infix fun LocalDate.withIndFormat(style: FormatStyle): String =
    withFormat(style, LOCALE_IND)

infix fun LocalDate.withFormat(pattern: String): String =
    withFormat(pattern, Locale.getDefault())

fun LocalDate.withFormat(pattern: String, locale: Locale = Locale.getDefault()): String =
    format(DateTimeFormatter.ofPattern(pattern, locale))

infix fun LocalDate.withFormat(style: FormatStyle): String =
    withFormat(style, Locale.getDefault())

fun LocalDate.withFormat(style: FormatStyle, locale: Locale = Locale.getDefault()): String =
    format(DateTimeFormatter.ofLocalizedDate(style).withLocale(locale))


//endregion
//region Manipulation

inline val LocalDate.toStartOfTheMonth: LocalDate
    get() = withDayOfMonth(1)
inline val LocalDate.toEndOfTheMonth: LocalDate
    get() = plusMonths(1).toStartOfTheMonth.minusDays(1)
inline val LocalDate.toStartOfTheYear: LocalDate
    get() = withDayOfYear(1)
inline val LocalDate.toEndOfTheYear: LocalDate
    get() = plusYears(1).toStartOfTheYear.minusDays(1)

//endregion
//region Conversion

infix fun LocalDate.withTime(localTime: LocalTime): LocalDateTime =
    LocalDateTime.of(this, localTime)

fun LocalDate.sinceYesterday(): LocalDateRange =
    minusDays(1) to this

fun LocalDate.sincePreviousWeek(): LocalDateRange =
    this to minusDays(DAYS_OF_WEEK.toLong())

fun LocalDate.sincePreviousMonth(): LocalDateRange =
    this to minusMonths(1)

fun LocalDate.sincePreviousYear(): LocalDateRange =
    this to minusYears(1)

fun LocalDate.untilTomorrow(): LocalDateRange =
    this to plusDays(1)

fun LocalDate.untilNextWeek(): LocalDateRange =
    this to plusDays(DAYS_OF_WEEK.toLong())

fun LocalDate.untilNextMonth(): LocalDateRange =
    this to plusMonths(1)

fun LocalDate.untilNextYear(): LocalDateRange =
    this to plusYears(1)

//endregion
//region Checking

inline val LocalDate.isToday: Boolean
    get() = equals(TODAY)

inline val LocalDate.isPast: Boolean
    get() = isBefore(TODAY)

inline val LocalDate.isFuture: Boolean
    get() = isAfter(TODAY)

inline val LocalDate.isTomorrow: Boolean
    get() = minusDays(1) == TODAY

inline val LocalDate.isYesterday: Boolean
    get() = plusDays(1) == TODAY

inline val LocalDate.isWeekend: Boolean
    get() = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY

inline val LocalDate.isWeekDay: Boolean
    get() = !isWeekend

infix fun LocalDate.isWithin(dateRange: LocalDateRange): Boolean =
    this == dateRange.first || this == dateRange.second || isWithinExclusive(dateRange)

infix fun LocalDate.isWithinExclusive(dateRange: LocalDateRange): Boolean =
    isBefore(dateRange.second) && isAfter(dateRange.first)

//endregion

