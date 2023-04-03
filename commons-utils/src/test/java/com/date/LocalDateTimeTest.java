package com.date;

import cn.hutool.core.date.DatePattern;
import com.yk.util.DateTimeUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/26 11:42:20
 */
public class LocalDateTimeTest
{
    @Test
    public void test()
    {
        String time = LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        // JDK8-BUG cannot format yyyyMMddHHmmssSSS
        LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        //LocalDateTime 转 Date
        LocalDateTime localDateTime2 = LocalDateTime.now();
        Date date = Date.from(localDateTime2.atZone(ZoneId.systemDefault()).toInstant());

        //Date 转 LocalDateTime
        Date startDate = new Date();
        LocalDateTime localDateTime1 = startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        System.out.println();
    }

    @Test
    public void testDate()
    {
        String dateString = "2023-02-13 10:12:09";
        LocalDateTime localDateTime = DateTimeUtils.parse(dateString, DateTimeUtils.NORM_DATETIME_FORMATTER);
        System.out.println(localDateTime);
        LocalDate localDate = DateTimeUtils.parseLocalDate(dateString, DateTimeUtils.NORM_DATETIME_FORMATTER);
        System.out.println(localDate);
        LocalTime localTime = DateTimeUtils.parseLocalTime(dateString, DateTimeUtils.NORM_DATETIME_FORMATTER);
        System.out.println(localTime);

        //
        java.sql.Date _sqlDate = new java.sql.Date(new Date().getTime());
        long lo = _sqlDate.getTime();
        String _str = DateTimeUtils.parse(lo).format(DateTimeUtils.NORM_DATETIME_FORMATTER);
        System.out.println(_str);

        // java.sql.Date.toInstant();java.sql.Timme.toInstant(); 都会产生异常
        java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());
        String _str2 = sqlDate.toLocalDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        System.out.println(_str2);

        java.sql.Time sqlTime = new java.sql.Time(new Date().getTime());
        String _str3 = sqlTime.toLocalTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
        System.out.println(_str3);


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        LocalDateTime localDateTime1 = cal.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        cal = Calendar.getInstance();
        cal.setTime(sqlDate);
        LocalDateTime localDateTime2 = cal.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        cal = Calendar.getInstance();
        cal.setTime(sqlTime);
        LocalDateTime localDateTime3 = cal.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println();
    }
}
