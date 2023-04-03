package com;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/10/27 09:58:16
 */
public class DataService
{
    public interface ILibrarySub extends ILibrary
    {
        static DataService data = new DataService();
        ILibrary INSTANCE = new LibraryImpl();
    }

    public ILibrary get()
    {
        return ILibrarySub.INSTANCE;
    }

    public void print()
    {
        ILibrarySub.INSTANCE.print();
    }
}
