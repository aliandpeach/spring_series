package com.yk.bitcoin;

import org.junit.Test;

/**
 * ThreadPrintTest
 */

public class ThreadPrintTest
{
    private static final int MAX = 100;
    
    private static int current = 1;

    private static final long KB = 1024;

    private static final long MB = 1024 * 1024;

    private static final long GB = 1024 * 1024 * 1024;

    @Test
    public void print() throws InterruptedException
    {
        System.out.println(((GB - 2) / GB));
    }

    // junit主线程退出后 t1 线程也跟着退出 不同于main函数，所以这里需要join
    @Test
    public void addPrint() throws InterruptedException
    {
        Runnable runnable = new Runner();
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        Thread t3 = new Thread(runnable);
        Thread t4 = new Thread(runnable);
        
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        
        t1.join();
        t2.join();
        t3.join();
        t4.join();
    }
    
    private static class Runner implements Runnable
    {
        
        @Override
        public void run()
        {
            while (current < MAX)
            {
                synchronized (this)
                {
                    if (current >= MAX)
                    {
                        return;
                    }
                    System.out.println(current++);
                }
            }
        }
    }
    
//    @Test
//    public void downloadTest()
//    {
//        long start = System.currentTimeMillis();
//
//        SimpleResult<List<ScanFile>> tree = new NutStore(config).listFiles(Collections.singletonList("/DBS_S/1文档特征"));
//        assert tree != null;
//        assert tree.getData() != null;
//        Optional.of(tree).ifPresent(result -> System.out.println(result.getData().size()));
//
//        ExecutorService executor = Executors.newFixedThreadPool(100);
//
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//        Optional.of(tree).ifPresent(t -> Optional.ofNullable(t.getData()).ifPresent(tt -> tt.forEach(ttt ->
//        {
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> doDownload(ttt), executor);
//
//            futures.add(future);
//        })));
//        CompletableFuture<Void>[] ary = futures.toArray(new CompletableFuture[0]);
//        CompletableFuture.allOf(ary).join();
//        out("over!!!");
//        long end = System.currentTimeMillis();
//        out(tree.getData().size() + " files, use time : " + (end - start) / 1000);
//    }
//
//    private void doDownload(ScanFile scanFile)
//    {
//        SimpleResult<List<FileResult>> fileResult = new NutStore(config).download(scanFile.getFilePath());
//        Optional.ofNullable(fileResult).ifPresent(result -> Optional.ofNullable(result.getData()).ifPresent(file -> file.forEach(each ->
//        {
//            System.out.println(each.getFileFingerPrint() + " " + scanFile.getSize() + " " + scanFile.getFileName());
//            try (FileOutputStream out = new FileOutputStream("D:\\opt\\" + System.currentTimeMillis() + "_" + scanFile.getFileName()))
//            {
//                out.write(each.getContent());
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//            each.setContent(null);
//
//            try
//            {
//                Thread.sleep(5000);
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//        })));
//    }
}
