package com.date;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
}
