package com;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/10/27 09:58:16
 */
public class LibraryImpl implements ILibrary
{

    public LibraryImpl()
    {
        System.out.println("LibraryImpl: " + System.currentTimeMillis());
    }

    public void print()
    {
        System.out.println("impl: " + System.currentTimeMillis());
    }
}
