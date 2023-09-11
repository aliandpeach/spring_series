package com.calc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CalcTest
{
    @Test
    public void test()
    {
        int size = (int) Math.ceil(215267254 / 10000 / 4);
        int size2 = (int) Math.ceil(215267254 / 10000 / size);
        System.out.println(size);
        System.out.println(size2);

        System.out.println(Math.round(11.4));//11
        System.out.println(Math.round(11.5));//12
        System.out.println(Math.round(-11.4));//-11
        System.out.println(Math.round(-11.5));//-11

        System.out.println(Math.round(11.6));//12
        System.out.println(Math.round(-11.6));//-12

        System.out.println(Math.round(0.5));//1
        System.out.println(Math.round(-0.5));//0

        System.out.println();

        int totalRecord = 999999;
        int prefetch = 10000;
        System.out.println("count: " + (totalRecord % prefetch == 0 ? totalRecord / prefetch : totalRecord / prefetch + 1));
        int partitionSize = (int) Math.ceil((double) totalRecord / prefetch / 4);
        System.out.println("partitionSize: " + partitionSize);
        int readFinish = (int) Math.ceil((double) totalRecord / prefetch / partitionSize);
        System.out.println("readFinish: " + readFinish);

        int co = 0;
        for (int i = 0; i < 4; i++)
        {
            int beginRow = i * partitionSize * prefetch;
            if (beginRow >= totalRecord)
            {
                break;
            }
            int endRow = (i + 1) * partitionSize * prefetch;
            if (endRow > totalRecord)
            {
                endRow = totalRecord;
            }

            System.out.println("start: " + beginRow + ", end: " + endRow
                    + ", " + ((endRow - beginRow) % prefetch == 0 ? ((endRow - beginRow) / prefetch) : ((endRow - beginRow) / prefetch + 1)));
            co += ((endRow - beginRow) % prefetch == 0 ? ((endRow - beginRow) / prefetch) : ((endRow - beginRow) / prefetch + 1));
        }

        System.out.println(co);
    }
}
