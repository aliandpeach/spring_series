package com.yk.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Date;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;
import static cn.hutool.core.date.DatePattern.PURE_DATETIME_PATTERN;
import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;

/**
 * DateTimeUtils
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/27 15:45
 */

public class DateTimeUtils
{
    /**
     * 标准日期格式 {@link DateTimeFormatter}：yyyyMMddHHmmssSSS
     */
    public static final DateTimeFormatter PURE_DATETIME_MS_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendPattern(PURE_DATETIME_PATTERN)
                    .appendValue(ChronoField.MILLI_OF_SECOND, 3)
                    .toFormatter();
    /**
     * 标准日期格式 {@link DateTimeFormatter}：yyyyMMdd
     */
    public static final DateTimeFormatter PURE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern(PURE_DATE_PATTERN);
    /**
     * 标准日期格式 {@link DateTimeFormatter}：yyyy-MM-dd
     */
    public static final DateTimeFormatter NORM_DATE_FORMATTER =
            DateTimeFormatter.ofPattern(NORM_DATE_PATTERN);
    /**
     * 标准日期格式：HHmm
     */
    public static final String TIME_PATTERN = "HHmm";
    /**
     * 标准日期格式 {@link DateTimeFormatter} HHmm
     */
    public static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern(TIME_PATTERN);
    /**
     * 标准日期格式：HH:mm
     */
    public static final String NORM_TIME_PATTERN = "HH:mm";

    /**
     * 标准日期格式 {@link DateTimeFormatter} HH:mm
     */
    public static final DateTimeFormatter NORM_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(NORM_TIME_PATTERN);

    /**
     * 标准日期时间格式，精确到秒 {@link DateTimeFormatter}：yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter NORM_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);

    /**
     * 横线分隔日期时间格式：yyyy-MM-dd-HH-mm-ss-
     */
    public static final String HORIZONTAL_LINE_PATTERN = "yyyy-MM-dd-HH-mm-ss-";

    /**
     * 横线分隔日期时间格式，精确到秒 {@link DateTimeFormatter}：yyyy-MM-dd-HH-mm-ss-
     */
    public static final DateTimeFormatter HORIZONTAL_LINE_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(HORIZONTAL_LINE_PATTERN);

    /**
     * 上海时区格式
     */
    public static final String CTT = ZoneId.SHORT_IDS.get("CTT");

    /**
     * 上海时区
     */
    public static final ZoneId CTT_ZONE_ID = ZoneId.of(CTT);


    private DateTimeUtils()
    {
    }

    /**
     * 获取当前时间，默认为上海时区
     *
     * @return Now LocalDateTime
     */
    public static LocalDateTime now()
    {
        return now(CTT_ZONE_ID);
    }

    /**
     * 根据时区获取当前时间
     *
     * @param zoneId 时区
     * @return Now LocalDateTime
     */
    public static LocalDateTime now(ZoneId zoneId)
    {
        return LocalDateTime.now(zoneId);
    }

    /**
     * LocalDateTime 格式化为字符串日期
     *
     * @param localDateTime 时间
     * @param formatter     时间格式
     * @return String
     */
    public static String format(LocalDateTime localDateTime, DateTimeFormatter formatter)
    {
        return localDateTime.format(formatter);
    }

    /**
     * LocalTime 格式化为字符串日期
     *
     * @param localTime 时间
     * @param formatter 时间格式
     * @return String
     */
    public static String format(LocalTime localTime, DateTimeFormatter formatter)
    {
        return localTime.format(formatter);
    }

    /**
     * LocalDate 格式化为字符串日期
     *
     * @param localDate 日期
     * @param formatter 日期格式
     * @return String
     */
    public static String format(LocalDate localDate, DateTimeFormatter formatter)
    {
        return localDate.format(formatter);
    }

    /**
     * 字符串日期解析为 LocalDateTime
     *
     * @param formatter 时间格式
     * @param time      时间
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String time, DateTimeFormatter formatter)
    {
        return LocalDateTime.parse(time, formatter);
    }

    /**
     * 字符串日期解析为 LocalDate
     *
     * @param formatter 时间格式
     * @param time      时间
     * @return LocalDate
     */
    public static LocalDate parseLocalDate(String time, DateTimeFormatter formatter)
    {
        return LocalDateTime.parse(time, formatter).toLocalDate();
    }

    /**
     * 字符串日期解析为 LocalTime
     *
     * @param formatter 时间格式
     * @param time      时间
     * @return LocalTime
     */
    public static LocalTime parseLocalTime(String time, DateTimeFormatter formatter)
    {
        return LocalDateTime.parse(time, formatter).toLocalTime();
    }

    /**
     * LocalDateTime转换 Date
     *
     * @param localDateTime localDateTime
     * @return Date
     */
    public static Date from(LocalDateTime localDateTime)
    {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date转换 LocalDateTime
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 转换long型日期为 LocalDateTime
     *
     * @param millis millis
     * @return LocalDateTime
     */
    public static LocalDateTime parse(long millis)
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    /**
     * 转换LocalDateTime 为long型日期
     *
     * @param localDateTime localDateTime
     * @return long
     */
    public static long parse(LocalDateTime localDateTime)
    {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * to instant by default zoneId(Shanghai)
     *
     * @param localDateTime 时间
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime localDateTime)
    {
        return toInstant(localDateTime, CTT_ZONE_ID);
    }

    /**
     * To instant by zoneId
     *
     * @param localDateTime 时间
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime localDateTime, ZoneId zoneId)
    {
        return localDateTime.atZone(zoneId).toInstant();
    }

    /**
     * 将 localDateTime 转为秒
     *
     * @param localDateTime 时间
     * @return 秒
     */
    public static long getEpochSecond(LocalDateTime localDateTime)
    {
        return toInstant(localDateTime).getEpochSecond();
    }

    /**
     * 将localDateTime 转为时间戳
     *
     * @param localDateTime 时间
     * @return 时间戳
     */
    public static long toEpochMilli(LocalDateTime localDateTime)
    {
        return toInstant(localDateTime).toEpochMilli();
    }

    /**
     * 将秒和毫秒设为0
     *
     * @param localDateTime 需要被设置的时间
     * @return 返回被设置的LocalDateTime
     */
    public static LocalDateTime secondAndNanoSetZero(LocalDateTime localDateTime)
    {
        return localDateTime.withSecond(0).withNano(0);
    }

    /**
     * 增加一天时间
     *
     * @param localDateTime 日期时间
     * @return 新增一天后的 LocalDateTime
     */
    public static LocalDateTime plusOneDayToDateTime(LocalDateTime localDateTime)
    {
        return plusDays(localDateTime, 1);
    }

    /**
     * 增加一天时间
     *
     * @param localDateTime 日期时间
     * @param localTime     时间
     * @return 新增一天后的 LocalDateTime
     */
    public static LocalDateTime plusOneDay(LocalDateTime localDateTime, LocalTime localTime)
    {
        return plusOneDay(localDateTime.toLocalDate(), localTime);
    }

    /**
     * 增加一天时间
     *
     * @param localDate 日期
     * @param localTime 时间
     * @return 新增一天后的 LocalDateTime
     */
    public static LocalDateTime plusOneDay(LocalDate localDate, LocalTime localTime)
    {
        final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return plusDays(localDateTime, 1);
    }


    /**
     * 增加一天时间
     *
     * @param localDateTime 日期时间
     * @return 新增一天后的 LocalDate
     */
    public static LocalDate plusOneDayToDate(LocalDateTime localDateTime)
    {
        return plusDays(localDateTime, 1).toLocalDate();
    }

    /**
     * 新增一天
     *
     * @param localDate 日期
     * @return 新增一天后的 LocalDate
     */
    public static LocalDate plusOneDayToDate(LocalDate localDate)
    {
        return plusDays(localDate, 1);
    }

    /**
     * 根据days新增天数
     *
     * @param localDateTime 日期时间
     * @param days          天数
     * @return 新增 days 后的 LocalDateTime
     */
    public static LocalDateTime plusDays(LocalDateTime localDateTime, long days)
    {
        return localDateTime.plusDays(days);
    }

    /**
     * 根据days新增天数
     *
     * @param localDate 日期
     * @param days      新增的天数
     * @return 新增 days 后的 LocalDate
     */
    public static LocalDate plusDays(LocalDate localDate, long days)
    {
        return localDate.plusDays(days);
    }

    /**
     * 增加1分钟
     *
     * @param localDateTime 日期时间
     * @return 返回新增的 LocalDateTime
     */
    public static LocalDateTime plusOneMinute(LocalDateTime localDateTime, LocalTime localTime)
    {
        return plusOneMinute(localDateTime.toLocalDate(), localTime);
    }

    /**
     * 增加1分钟
     *
     * @param localDate 日期
     * @param localTime 时间
     * @return 返回新增的 LocalDateTime
     */
    public static LocalDateTime plusOneMinute(LocalDate localDate, LocalTime localTime)
    {
        final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return plusMinutes(localDateTime, 1);
    }


    /**
     * 增加1分钟
     *
     * @param localDateTime 日期时间
     * @return 返回新增的 LocalDateTime
     */
    public static LocalDateTime plusOneMinute(LocalDateTime localDateTime)
    {
        return plusMinutes(localDateTime, 1);
    }

    /**
     * 新增1分钟
     *
     * @param localTime 日期时间
     * @return 返回新增的 LocalTime
     */
    public static LocalTime plusOneMinute(LocalTime localTime)
    {
        return plusMinutes(localTime, 1);
    }

    /**
     * 增加30分钟
     *
     * @param localDateTime 日期时间
     * @return 返回新增的 LocalDateTime
     */
    public static LocalDateTime plusThirtyMinute(LocalDateTime localDateTime)
    {
        return plusMinutes(localDateTime, 30);
    }


    /**
     * 增加30分钟
     *
     * @param localTime 日期时间
     * @return 返回新增的 LocalTime
     */
    public static LocalTime plusThirtyMinute(LocalTime localTime)
    {
        return plusMinutes(localTime, 30);
    }

    /**
     * 新增1分钟
     *
     * @param localDateTime 日期时间
     * @return 返回新增的 LocalTime
     */
    public static LocalTime plusOneMinuteToTime(LocalDateTime localDateTime)
    {
        return plusMinutes(localDateTime, 1).toLocalTime();
    }

    /**
     * 根据 minutes 新增分钟
     *
     * @param localDateTime 日期时间
     * @param minutes       分钟数
     * @return 返回新增的 LocalDateTime
     */
    public static LocalDateTime plusMinutes(LocalDateTime localDateTime, long minutes)
    {
        return localDateTime.plusMinutes(minutes);
    }

    /**
     * 根据 minutes 新增分钟
     *
     * @param localTime 时间
     * @param minutes   分钟数
     * @return 返回新增的 LocalTime
     */
    public static LocalTime plusMinutes(LocalTime localTime, long minutes)
    {
        return localTime.plusMinutes(minutes);
    }

    /**
     * 减少 1 分钟
     *
     * @param localDateTime 日期时间
     * @param localTime     时间
     * @return 返回新增的 LocalTime
     */
    public static LocalDateTime minusOneMinutes(LocalDateTime localDateTime, LocalTime localTime)
    {
        return minusOneMinutes(localDateTime.toLocalDate(), localTime);
    }


    /**
     * 减少 1 分钟
     *
     * @param localDate 日期
     * @param localTime 时间
     * @return 返回新增的 LocalTime
     */
    public static LocalDateTime minusOneMinutes(LocalDate localDate, LocalTime localTime)
    {
        final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return minusMinutes(localDateTime, 1);
    }

    /**
     * 减少 1 分钟
     *
     * @param localDateTime 日期时间
     * @return 返回新增的 LocalTime
     */
    public static LocalDateTime minusOneMinutes(LocalDateTime localDateTime)
    {
        return minusMinutes(localDateTime, 1);
    }

    /**
     * 根据 minutes 减少分钟
     *
     * @param localDateTime 日期时间
     * @param minutes       分钟数
     * @return 返回新增的 LocalTime
     */
    public static LocalDateTime minusMinutes(LocalDateTime localDateTime, long minutes)
    {
        return localDateTime.minusMinutes(minutes);
    }

    /**
     * 根据 minutes 减少分钟
     *
     * @param localTime 时间
     * @param minutes   分钟数
     * @return 返回新增的 LocalTime
     */
    public static LocalTime minusMinutes(LocalTime localTime, long minutes)
    {
        return localTime.minusMinutes(minutes);
    }

    /**
     * 判断是否是中午
     *
     * @param startInclusive Start
     * @return boolean
     */
    public static boolean isNoon(LocalDateTime startInclusive)
    {
        LocalDateTime noonDateTime = LocalDateTime.of(startInclusive.toLocalDate(), LocalTime.NOON);
        return Duration.between(startInclusive, noonDateTime).isZero();
    }

    /**
     * 判断是否是中午
     *
     * @param startInclusive Start
     * @return boolean
     */
    public static boolean isNoon(LocalTime startInclusive)
    {
        return Duration.between(startInclusive, LocalTime.NOON).isZero();
    }

    /**
     * 是否是负数，startInclusive 大于 endInclusive 就是负数
     *
     * @param startInclusive Start
     * @param endInclusive   end
     * @return boolean
     */
    public static boolean isNegative(Temporal startInclusive, Temporal endInclusive)
    {
        return Duration.between(startInclusive, endInclusive).isNegative();
    }

    /**
     * 相比是否是0，两个时间一致就是0
     *
     * @param startInclusive Start
     * @param endInclusive   end
     * @return boolean
     */
    public static boolean isZero(Temporal startInclusive, Temporal endInclusive)
    {
        return Duration.between(startInclusive, endInclusive).isZero();
    }

    /**
     * endInclusive 大于或等于 startInclusive
     *
     * @param startInclusive Start
     * @param endInclusive   end
     * @return boolean
     */
    public static boolean isGreaterOrEqual(Temporal startInclusive, Temporal endInclusive)
    {
        return Duration.between(startInclusive, endInclusive).toNanos() >= 0;
    }

    /**
     * endInclusive 大于 startInclusive
     *
     * @param startInclusive Start
     * @param endInclusive   end
     * @return boolean
     */
    public static boolean isGreater(Temporal startInclusive, Temporal endInclusive)
    {
        return Duration.between(startInclusive, endInclusive).toNanos() > 0;
    }

    /**
     * endInclusive 小或等于 startInclusive
     *
     * @param startInclusive Start
     * @param endInclusive   end
     * @return boolean
     */
    public static boolean isLessThanOrEqual(Temporal startInclusive, Temporal endInclusive)
    {
        return Duration.between(startInclusive, endInclusive).toNanos() <= 0;
    }

    /**
     * 按 yyyyMMdd 格式化
     *
     * @param localDateTime 日期时间
     * @return Result
     */
    public static String formatDate(LocalDateTime localDateTime)
    {
        return format(localDateTime, PURE_DATE_FORMATTER);
    }

    /**
     * 按 yyyyMMdd 格式化
     *
     * @param localDate 日期
     * @return Result
     */
    public static String formatDate(LocalDate localDate)
    {
        return format(localDate, PURE_DATE_FORMATTER);
    }

    /**
     * 按 HHmm 格式化
     *
     * @param localDateTime 时间
     * @return Result
     */
    public static String formatTime(LocalDateTime localDateTime)
    {
        return format(localDateTime, TIME_FORMATTER);
    }

    /**
     * 按 HHmm 格式化
     *
     * @param localTime 时间
     * @return Result
     */
    public static String formatTime(LocalTime localTime)
    {
        return format(localTime, TIME_FORMATTER);
    }

    /**
     * 按 yyyy-MM-dd HH:mm:ss 格式格式化
     *
     * @param localDateTime 时间
     * @return Result
     */
    public static String formatDateTime(LocalDateTime localDateTime)
    {
        return format(localDateTime, NORM_DATETIME_FORMATTER);
    }

    /**
     * 按照上海时区，解析香港格式的时间
     * <p>
     * 时间格式 yyyyMMddHHmmssSSS
     *
     * @param time 时间
     * @return LocalDateTime
     */
    public static LocalDateTime parseCttDateTime(String time)
    {
        return parse(time, PURE_DATETIME_MS_FORMATTER);
    }
}
