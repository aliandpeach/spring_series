package com.date;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/08/16 14:09:14
 */
public class CalendarTest
{
    public static void main(String[] args)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 191000000);

        Date date = calendar.getTime();

        /*int year = calendar.get(Calendar.YEAR);
        System.out.println(year);
        calendar.set(Calendar.YEAR, 9900);
        year = calendar.get(Calendar.YEAR);
        System.out.println(year);

        int month = calendar.get(Calendar.MONTH);
        System.out.println(month);
        calendar.set(Calendar.MONTH, 11);
        month = calendar.get(Calendar.MONTH);
        System.out.println(month);*/

        Date _date = calendar.getTime();
        System.out.println(_date);

        if (calendar.get(Calendar.YEAR) >= 9900)
        {
            calendar.set(Calendar.YEAR, 9900);
        }
        if (calendar.get(Calendar.YEAR) <= 1000)
        {
            calendar.set(Calendar.YEAR, 1000);
        }

        _date = calendar.getTime();
        System.out.println(_date);
    }
}
